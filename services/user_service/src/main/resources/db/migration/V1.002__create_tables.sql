begin;

create table
    if not exists app_user (
        id uuid not null,
        username varchar(255),
        display_name varchar(255),
        email varchar(255),
        avatar varchar(255),
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    );
    
commit;