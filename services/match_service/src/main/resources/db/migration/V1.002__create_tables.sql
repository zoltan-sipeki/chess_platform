begin;

create table
    if not exists match_user (
        id uuid,
        display_name varchar(255) not null,
        avatar varchar(255),
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    );


create table 
    if not exists player_mmr (
        id uuid not null,
        user_id uuid not null,
        unranked_mmr int not null,
        ranked_mmr int not null,
        last_played timestamp with time zone,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id),
        foreign key (user_id) references match_user (id)
);

create table
    if not exists match (
        id uuid,
        started_at timestamp with time zone,
        ended_at timestamp with time zone,
        duration bigint not null,
        type varchar(255) not null,
        replay text not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    );

create table
    if not exists match_detail (
        id uuid not null,
        user_id uuid not null,
        match_id uuid not null,
        color varchar(255) not null,
        score varchar(255) not null,
        mmr_before int,
        mmr_after int,
        mmr_change int,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id),
        unique (user_id, match_id),
        foreign key (user_id) references match_user (id),
        foreign key (match_id) references match (id)
    );

create table
    if not exists match_stat (
        id uuid not null,
        user_id uuid not null,
        match_type varchar(255) not null,
        games_played int not null default 0,
        wins int not null default 0,
        draws int not null default 0,
        losses int not null default 0,
        win_ratio float not null default 0,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id),
        unique (user_id, match_type),
        foreign key (user_id) references match_user (id)
    );

create table
    if not exists ongoing_match (
        id uuid not null,
        match_id bigint not null,
        user_id uuid not null,
        target varchar(255) not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id),
        unique (user_id)
    );

create table 
    if not exists privacy_setting (
        id uuid not null,
        user_id uuid not null,
        resource varchar(255) not null,
        restriction varchar(255) not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id),
        unique (user_id, resource),
        foreign key (user_id) references match_user (id)
    );

commit;