begin;

create table
    if not exists player (
        id uuid not null,
        display_name varchar(255) not null,
        avatar varchar(255) not null,
        unranked_mmr int not null,
        ranked_mmr int not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    );

create table if not exists match_routing (
    id uuid not null,
    player_id uuid not null,
    match_id bigint not null,
    match_type varchar(255) not null,
    mmr int,
    inviter_id uuid,
    invitee_id uuid,
    target uuid not null,
    expires_at timestamp with time zone not null,
    token varchar(2000) not null,
    match_status varchar(255) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    primary key (id),
    unique (player_id),
    foreign key (player_id) references player (id),
    foreign key (inviter_id) references player (id),
    foreign key (invitee_id) references player (id)
);

commit;