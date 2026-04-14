begin;

create table
    if not exists matchmaking_user (
        id uuid not null,
        unranked_mmr int not null,
        ranked_mmr int not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    );

commit;