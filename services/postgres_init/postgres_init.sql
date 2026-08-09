CREATE DATABASE chat_db;
CREATE DATABASE chess_db;
CREATE DATABASE match_db;
CREATE DATABASE matchmaking_db;
CREATE DATABASE relay_db;
CREATE DATABASE user_db;
CREATE DATABASE keycloak_db;

-- password must be specified
CREATE USER chat_service WITH PASSWORD 'dummy_password' LOGIN;
CREATE USER chess_service WITH PASSWORD 'dummy_password' LOGIN;
CREATE USER match_service WITH PASSWORD 'dummy_password' LOGIN;
CREATE USER matchmaking_service WITH PASSWORD 'dummy_password' LOGIN;
CREATE USER relay_service WITH PASSWORD 'dummy_password' LOGIN;
CREATE USER user_service WITH PASSWORD 'dummy_password' LOGIN;
CREATE USER keycloak WITH PASSWORD 'dummy_password' LOGIN;

\c chat_db;
GRANT CREATE, USAGE ON SCHEMA public TO chat_service;
\c chess_db;
GRANT CREATE, USAGE ON SCHEMA public TO chess_service;
\c match_db;
GRANT CREATE, USAGE ON SCHEMA public TO match_service;
\c matchmaking_db;
GRANT CREATE, USAGE ON SCHEMA public TO matchmaking_service;
\c relay_db;
GRANT CREATE, USAGE ON SCHEMA public TO relay_service;
\c user_db;
GRANT CREATE, USAGE ON SCHEMA public TO user_service;
\c keycloak_db;
GRANT CREATE, USAGE ON SCHEMA public TO keycloak;

