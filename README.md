# Chess Platform - Microservices

## Table of Contents

1. [Features](#features)
2. [High-level architecture](#high-level-architecture)
    1. [The Event Framework](#the-event-framework)
    2. [Keycloak / User Service](#keycloak--user-service)
    3. [Relay Service](#relay-service)
    4. [Chat Service](#chat-service)
    5. [Match Service](#match-service)
3. [The matchmaking / live game subsystems](#the-matchmaking--live-game-subsystems)
    1. [High-level overview](#high-level-overview)
    2. [Design decisions / trade-offs](#design-decisions--trade-offs)
4. [Known gaps](#known-gaps)

## Features

- User authentication. The site is usable by registered users only.
- The game of chess with all the rules implemented. Draws are simplified compared to the standard rules.
- Automated public matchmaking system for both competitive (ranked) and non-competitive (unranked) games, based on the [Elo rating system](https://en.wikipedia.org/wiki/Elo_rating_system) to match players and evaluate their skill levels.
- Private matchmaking: friends can invite each other to play together.
- Reconnect: if a player has been disconnected or closed the app, they can reconnect to their ongoing game.
- Match replays for analyzing past gameplay.
- Leaderboard, based on the ranked, public MMR/Elo rating of the players (MMR - matchmaking rating). (The unranked MMR is hidden from the players. It is only used by the system to match players for unranked games.)
- Friend system: users can send, accept and reject friend requests or can remove (but not block) friends.
- Notifications.
- Real-time chat:
    - Friends can message each other.
    - Emoji and user status support (online, offline, away, in-game, looking for match).
    - Typing indicators.
    - Chat history.
    - Highlighting of unread messages.
- User profiles:
    - Player statistics: the time a player registered, what was the last time they were online, ranked MMR, percentile, longest winning/losing streak.
    - Game statistics: how many games a player has played of each type in total (ranked, unranked, both added together), how many of them have been wins, losses or draws. Win ratio.
    - Filterable friend list and match history.
    - Profiles can be looked up by display names.

- User settings:
    - Display name, avatar, password and e-mail address can be changed.
    - Privacy settings: a user can control who can see parts, or all of their profile: only themselves, only friends, or everybody.
    - Accounts can be deleted permanently.

## High-level architecture

<img width="1100" height="850" alt="highlevel_architecture" src="https://github.com/user-attachments/assets/fd1cee43-9b7b-4282-a935-32f5e612e947" />  

*Figure 1.: High-level architecture*

The backend is made up of 7 services: Keycloak, user, match, chess, matchmaking, relay and chat services. Each service is backed by their own, separate PostgreSQL database. The services, with a few exceptions, are entirely event-driven, with RabbitMQ facilitating communication between them. The Angular frontend can talk to the services via the API gateway, implemented with Spring Cloud Gateway. Spring Cloud Eureka is used for service discovery, and Spring Cloud Config Server for centralized configuration management. Since I wanted to learn about NoSQL databases including their pros and cons and when to use them instead of a traditional RDBMS, I also experimented with MongoDB. The services are orchestrated with Docker Compose.

You might be thinking that this project is overengineered and overcomplicated: who would go about implementing a multiplayer online chess site as a multi-service system without knowing how each part of the application should be scaled, or whether it needed scaling at all? You would be absolutely right if you were thinking that. The whole application could have been implemented as a well-modularized monolith without the additional complexities of inter-service communication, eventual consistency, and system failure handling. However, the complexity is intentional as my goal in developing this particular application was to teach myself microservices and distributed systems concepts, which would have been impossible had I not introduced these complexities.

### The Event Framework

The event system is implemented with the transactional outbox pattern: events, in addition to any related data, are committed to the database in one transaction, and, if the commit was successful, immediately published to RabbitMQ. Events whose purpose is not solely to provide live updates to the client must be acknowledged. The system is idempotent, eventually consistent and guarantees at least once delivery: if an acknowledgement does not arrive in a specified amount of time, the event is republished. Also, if an event has already been processed by the receiving party, but somehow the acknowledgement got lost, a new acknowledgement will be sent in response to the next delivery, but without reprocessing the event. Event tracking is entirely hand-rolled, it does not use RabbitMQ-specific features, such as publisher confirms and message acknowledgements. This makes the implementation message-broker-agnostic.

### Keycloak / User Service

Keycloak is the identity provider, authentication mechanism behind the application. Keycloak implements OpenID Connect (OAuth 2.0) as its authentication protocol. Users directly register with Keycloak. Once a user has successfully registered, an event containing the registered user's data is immediately published by Keycloak to a RabbitMQ exchange, a mechanism implemented with a custom Keycloak SPI. The event then is picked up by the user service to save the user in its own database, and to republish it to all interested services. Should the event get lost (e.g.: the Keycloak instance registering the user crashes between committing the user to the database and publishing the event, the user service or RabbitMQ becomes unavailable), the user service periodically polls Keycloak for unsynced users. The event system framework above could have been implemented as another Keycloak SPI to eliminate the polling, but according to the documentation, adding custom JPA entities to Keycloak is possible but unsupported, meaning there is no guarantee that it won't be removed or changed in the future without warning.

In addition, the user service is a CRUD service, supporting avatar management (upload, delete), and updating a user (such as updating the display name of a user). Nothing other than the username and e-mail is stored by Keycloak.

**Access token handling:**
Keycloak issuess access tokens as JWTs. The access/refresh tokens are stored in memory on the client, not in local storage to mitigate XSS attacks. The authentication workflow is as follows:

After the page has loaded, the client immediately redirects the user to Keycloak. Since a public client such as our Angular frontend can't store the client secret securely, the OAuth 2.0 authorization flow used is the Authorization Code grant with the PKCE extension. If there is an active session with Keycloak, Keycloak immediately redirects the browser to the application's callback URL with the authorization code associated with that active session. The application then exchanges the authorization code for an access/refresh token, which are in turn stored in memory, and the user is allowed to use the application. If there is no active session, Keycloak asks the user for their credentials. If they are correct, the application obtains an access/refresh token as before, if not, the user is denied access. In subsequent API calls, the application refreshes the access token using the refresh token around the time it expires without asking the user.

Storing the access/refresh tokens in local storage would eliminate the need to always redirect to Keycloak when the page is loaded, making the load time slightly faster and the user experience slightly better, but it would also expose the application to XSS attacks, where an attacker could steal the access token.

### Relay Service

A websocket service that pushes live updates to clients. Instead of all services with live updates maintaining a websocket connection with the client, they send their updates as "broadcast events" via RabbitMQ to the relay service, which in turn forwards the events to the clients, centralizing push notifications in one service. All nodes of the relay service receive the broadcast events since it is not known ahead of time which node a recipient is connected to. Broadcast events are events such as the MessageCreatedEvent (fired when a user posts a message in a channel), or UserUpdatedEvent (sent when a user updates their display name / avatar) etc.

After a websocket connection has been established, the client authenticates by sending a message of type AUTHENTICATE with the Keycloak access token as the payload. (If the client does not authenticate in a specified amount of time, the websocket connection is closed.) The service verifies the access token, and if the verification is successful, the connection remains open and the client can then start receiving updates. If the user has provided an invalid access token, the connection is closed immediately.

Besides, the service manages user presence (online, offline, away).

How does the relay service know which clients a broadcast event should be forwarded to if it does not have the list of recipients? Some events do contain recipient information, but some do not. For events that do not contain a recipient list, the service obtains it by calling a chat service endpoint that returns the contacts of the sender. Another way to solve this problem would be to replicate the contact info between the two services. Both have their pros and cons.

### Chat Service
A CRUD service that provides a REST API for channel handling, instant messaging, friend requests, notifications, and related queries. It also provides privacy settings for querying friend data.

### Match Service
A query service that provides a REST API for querying past match data, replays, game/player statistics and privacy settings. All this data is obtained by the chess service sending an event when a match has ended. All match data is owned by the match service.

## The matchmaking / live game subsystems

The matchmaking service never touches game state, it's only job is to pair players together, and issue "matchmaking tokens" / tickets to players so that they can connect to a pre-selected chess node hosting the game. It also acts as a match routing service for when a player needs to reconnect to an ongoing game. The sole job of the chess nodes are to host games, they are not meant to be queried.

In the following sections, I will give an overview of the workings of the two systems and why I designed them the way I did.

### High-level overview

<img width="850" height="609" alt="matchmaking_highlevel" src="https://github.com/user-attachments/assets/8d0631ab-b4aa-4c0d-b565-4007b51d0fea" />  

*Figure 2.: Matchmaking / live game architecture*

The client enters a matchmaking queue, calling the corresponding REST endpoint on the matchmaking service. There is a separate matchmaking queue for ranked and unranked games.

As is shown in Figure 3., when the player is placed into the queue, the system assigns an MMR range to the player, and tries to insert them into an in-memory binary tree sorted by MMR range. If there is a player with an overlapping MMR range in the tree, the system removes both players from the queue and queries Eureka for the UUID of an available chess service node in a round robin fashion. It then creates a so-called matchmaking token (or ticket) containing the data the chess service needs to create a match: a random 64-bit match ID generated by the matchmaking service, the player ID, the type of the match (ranked, unranked, private), the player's MMR (if applicable), and the UUID of the target chess service instance. Before the tokens are handed off to the players, they are cryptographically signed so that clients can't forge them. To make reconnecting to a game possible, the matchmaking service saves the match routing data to the database. If there aren't any players with an overlapping MMR range, the player is reinserted into the tree and sits there waiting for another player. If their time in the queue is up, they are removed from the queue, their MMR range is expanded, and then they are inserted back into the queue. This whole process is repeated in a loop until another compatible player is found, the player decides to leave the queue or the player disconnects.

Private games circumvent the matchmaking queues. They work by one player inviting another. Matchmaking tokens are generated and match routing data are saved for private games as well.

<img width="1247" height="586" alt="matchmaking_service" src="https://github.com/user-attachments/assets/ffbf2382-0f26-47c2-8b35-37b50f7bcaa4" />  

*Figure 3.: Matchmaking workflow*

As is shown in Figure 4., the client connects to a game by presenting their matchmaking token to the pre-selected chess service node. The client establishes a websocket connection with the API gateway, calling "/chess/ws?target={node-uuid}". The API gateway obtains the IP address of the target chess node by parsing out the node UUID from the target query parameter, and querying Eureka. If there is a node with the given UUID, the API gateway opens a websocket connection downstream with the chess service node, proxying the original connection, otherwise it terminates its connection with client. The client then has to go through the same authentication workflow as it does with the relay service. If authentication was successful, the client sends their matchmaking token in a JOIN_MATCH message to the chess service to be verified. The service verifies the token signature using the public key of the matchmaking service, then checks whether the player ID in the token matches the ID of the authenticated user, and whether the target node UUID matches its own. (Players reconnect with a JOIN_MATCH message as well, but token verification is skipped if a player already has a game in progress). If verification fails, the connection is closed, else the token is handed off to one of many coordinator threads, based on the match ID (the target thread is determined by taking the modulus of the match ID and the number of threads), confining a match to a single thread. Each subsequent match-related message is sharded by the match ID too, and thus handled by a single thread. If there is no match with the given ID, the match is created, the player is added to the match, and a connection timeout is started. If the other player does not connect before the timer runs out, the match is cleaned up, and the first player is disconnected. If both players are connected, the chess service notifies the matchmaking service that the match is now active, a chessboard is instantiated, a flag-fall timer is started (or a promotion timer if promotion is in progress), then the coordinator keeps accepting messages in an event-loop until the game is over. If so, a MatchEndedEvent is fired with all the match data, the match is cleaned up, and the players are disconnected. On receiving the event, the matchmaking service clears the routing data for the given match ID and updates the MMRs of the players, while the match service parses and persists the data.

<img width="2059" height="1209" alt="chess_service" src="https://github.com/user-attachments/assets/97776761-4853-453c-9fd3-3c204e00011e" />  

*Figure 4.: Game coordinator workflow*

### Design decisions / trade-offs
The matchmaking queues are implemented as in-memory binary trees (vanilla Java TreeSet). The biggest drawback of in-memory structures is that they can't be horizontally scaled, in which case we would need to spawn multiple nodes, and would need to move the queues out of memory into a shared database tables, right?. Does it really make sense to horizontally scale the queues, though?

- Implementing the queues as tables makes the queue operations slower because we are writing to disk.
- A matchmaking queue is a live data structure, which means its content can get stale very quickly, and does not make a lot of sense to persist. What happens if the matchmaking service goes down (it either crashes or is shut down for maintenance)? All the players who were standing in queue at the time of the crash or the shutdown will still be standing in queue when the service comes back online. By the time that happens, some or all of the players may already have logged out. This has a much lower chance of happening if there are more than one matchmaking nodes running, but this is something an in-memory structure naturally solves.
- A queue player object that's stored in the queues take up around 250 B, which means if we have 16 GB of available memory, one node can enqueue 68 million players at the same time, and since the queues are implemented as binary trees, both the lookup and insertion/deletion take O(log n) time, which is only 24 comparisons in the worst case. Although the aforementioned number of players is not reasonable in practice, it shows that neither memory nor the CPU will be the bottleneck here.
- The only possible bottleneck is the number of concurrent connections. But as each queue operation is very fast, would it really become a problem, with the operations implemented as REST calls, not via a permanent websocket connection? Only real world testing would answer this question. The players are notified of whether a game is ready via the relay service (through websocket), which theoratically means the relay service would need scaling before the matchmaking service would. But in the event concurrent connections did become the bottleneck, I still would not increase the number of matchmaking nodes, and would not move the queue into a database. I would instead introduce connection nodes whose sole job would be to accept incoming requests from clients, and to funnel the requests to one instance of the matchmaking service via a RabbitMQ exchange. It would retain the same benefits of an in-memory data structure, while allowing more concurrent connections.
- Most games with a matchmaking system are split into geographical regions, meaning a player connects to a server physically closest to them, and are paired with players from their region, to reduce latency. Latency is not very crucial for a chess game as it's an event-driven game, not a real-time one, but this is something that is also against the multiple nodes + database approach, and is in favor of an in-memory service.


**Why do clients connect to the chess service with a JWT? Why not have the matchmaking and chess services exchange match information and let players connect without a JWT instead?**

Exchanging information before the players connect introduces unnecessary bookkeeping and coupling between the services and makes everything more complicated than it needs to be. In this scenario, the matchmaking service has to send the match information to the chess service via a REST call (Emitting an asynchronous event might not be the best option here because the target chess service node has to be online for a game to happen anyway). Now we have to think about what we should do when an error occurs. What happens if the players connect to the chess service before it receives the match information? Should we let the players keep retrying until the chess node finally receives it and can allow the players to join?

A JWT solves all of these issues elegantly. It contains all the necessary information needed to create a match, and is cryptographically signed, meaning the client can't tamper with the data. It also naturally supports expiration, after which a player can no longer join a game with that specific JWT. Since clients have to connect to participate in a chess game anyway, it's completely natural for the players to present a "ticket", like in a movie theater. Sticking with the analogy, the box office is the matchmaking service, which issues tickets (JWTs) to viewers (players), who will later present them to the conductor (chess service). The chess service does not know anything about the matchmaking service, only about the JWT, and vice versa.

**Why the many coordinator threads?**

Each websocket connection uses a different I/O thread. If there weren't any dedicated coordinator threads, all match-related data structures would have to be guarded by a lock, which would reduce performance and introduce potential concurrency bugs. Confining match conduction logic to a single thread solves the locking issue: I/O thread converts incoming messages and then dispatches them to the designated coordinator thread for further processing. Messages are dispatched to coordinator threads by their assigned match ID, ensuring that a match is always handled by the same thread. More than one coordinator thread is probably not that necessary for a chess server, chess not being a CPU intensive game at all, and the potential bottleneck being network I/O. The additional threads are just for good measure, more threads in this case are not going to present any further complexities, so we can freely fine-tune the them if needed. In the current implementation, there are as many threads as there are CPU cores, which is the upper limit if we care about CPU performance.

The match-related data structures are all stored in memory, they are not persisted to a database. It is to ensure that response times are fast. If the server crashes, all games in progress at the time are lost, which is expected. This is not an MMORPG where the game state has to be periodically saved.

**Failure handling**

I've already touched on failure handling in the previous paragraphs, but what if a player tries to reconnect to a game, and the chess node hosting the game has died? The match routing data, which the player queries if they have an ongoing game, is owned by the matchmaking service. If a specific chess node dies, a player with an ongoing game on that node is not going to be able to play another game again because a player can't invite anyone to a private game, nor can they queue for a public game if they have a match in progress. The new chess node taking the place of the previous one will have a new UUID. We have a deadlock situation here. How does the matchmaking service know if a specific chess node is alive or dead? The matchmaking service periodically queries Eureka for the status of the chess nodes, and if Eureka declares a node dead, the matchmaking service will declare that node dead as well, and will delete all match routing data pertaining to that node, resolving the deadlock.


**Conclusions to the design**  

This particular architecture may be overkill for an online chess game, but I would definitely consider it if I had to write the backend for a performance-intensive multiplayer real-time game (such as an online shooter). It is crucial for such games to keep all processor cores running the game loop (multiple independent coordinator threads, capped at the number of processor cores). Moving the matchmaking system out into its own service is also a good idea in this case because the game server should only run the game logic and accept incoming connections to provide the best possible performance, it shouldn't be hindered by the workings of the matchmaking system.

## Known gaps

- No circuit-breaker, rate limiting, retry mechanism for synchronous REST calls.
- No distributed tracing, logging.
- No distributed locking for scheduled jobs.
- Error handling is hand-waved in a lot of cases.
- Missing caching layer.
- Authorization framework is not very well though-out, needs rework. Quary fragments should be kept.