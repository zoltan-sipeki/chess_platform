begin;

create table
    if not exists relay_user (
        id uuid not null,
        presence varchar(255) not null,
        preferred_presence varchar(255) not null,
        activity varchar(255),
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    ); 
    
commit;