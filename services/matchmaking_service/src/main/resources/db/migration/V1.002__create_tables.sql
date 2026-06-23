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

commit;