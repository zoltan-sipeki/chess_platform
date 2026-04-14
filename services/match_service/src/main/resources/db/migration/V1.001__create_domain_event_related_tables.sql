begin;

create table
    domain_event_data (
        id uuid not null,
        category varchar(255) not null,
        type varchar(255) not null,
        data text not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
    );

create table
    domain_event_ack (
        id uuid not null,
        event_id uuid not null,
        service_name varchar(255) not null,
        status varchar(255) not null,
        last_sent_at timestamp with time zone not null,
        acked_at timestamp with time zone,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id),
        foreign key (event_id) references domain_event_data (id)
    );

create table
    acked_domain_event (
        id uuid not null,
        category varchar(255) not null,
        type varchar(255) not null,
        data text not null,
        acked_at timestamp with time zone not null,
        created_at timestamp with time zone not null,
        updated_at timestamp with time zone,
        primary key (id)
);

commit;