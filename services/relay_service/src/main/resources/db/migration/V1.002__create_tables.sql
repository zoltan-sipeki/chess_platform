begin;

create table
    if not exists relay_user (
        id uuid not null,
        preferred_presence varchar(255) not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    ); 
    
commit;