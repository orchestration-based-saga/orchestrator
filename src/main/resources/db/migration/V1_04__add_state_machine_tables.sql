create table if not exists public.action
(
    id   bigint not null
        primary key,
    name varchar(255),
    spel varchar(255)
);

alter table public.action
    owner to postgres;

create table if not exists public.guard
(
    id   bigint not null
        primary key,
    name varchar(255),
    spel varchar(255)
);

alter table public.guard
    owner to postgres;

create table if not exists public.state
(
    initial_state     boolean,
    kind              smallint
        constraint state_kind_check
            check ((kind >= 0) AND (kind <= 9)),
    id                bigint not null
        primary key,
    initial_action_id bigint
        unique
        constraint fk_state_initial_action
            references public.action,
    parent_state_id   bigint
        constraint fk_state_parent_state
            references public.state,
    machine_id        varchar(255),
    region            varchar(255),
    state             varchar(255),
    submachine_id     varchar(255)
);

alter table public.state
    owner to postgres;

create table if not exists public.deferred_events
(
    jpa_repository_state_id bigint not null
        constraint fk_state_deferred_events
            references public.state,
    deferred_events         varchar(255)
);

alter table public.deferred_events
    owner to postgres;

create table if not exists public.state_entry_actions
(
    entry_actions_id        bigint not null
        constraint fk_state_entry_actions_a
            references public.action,
    jpa_repository_state_id bigint not null
        constraint fk_state_entry_actions_s
            references public.state,
    primary key (entry_actions_id, jpa_repository_state_id)
);

alter table public.state_entry_actions
    owner to postgres;

create table if not exists public.state_exit_actions
(
    exit_actions_id         bigint not null
        constraint fk_state_exit_actions_a
            references public.action,
    jpa_repository_state_id bigint not null
        constraint fk_state_exit_actions_s
            references public.state,
    primary key (exit_actions_id, jpa_repository_state_id)
);

alter table public.state_exit_actions
    owner to postgres;

create table if not exists public.state_machine
(
    machine_id            varchar(255) not null
        primary key,
    state                 varchar(255),
    state_machine_context oid
);

alter table public.state_machine
    owner to postgres;

create table if not exists public.state_state_actions
(
    jpa_repository_state_id bigint not null
        constraint fk_state_state_actions_s
            references public.state,
    state_actions_id        bigint not null
        constraint fk_state_state_actions_a
            references public.action,
    primary key (jpa_repository_state_id, state_actions_id)
);

alter table public.state_state_actions
    owner to postgres;

create table if not exists public.transition
(
    kind       smallint
        constraint transition_kind_check
            check ((kind >= 0) AND (kind <= 3)),
    guard_id   bigint
        constraint fk_transition_guard
            references public.guard,
    id         bigint not null
        primary key,
    source_id  bigint
        constraint fk_transition_source
            references public.state,
    target_id  bigint
        constraint fk_transition_target
            references public.state,
    event      varchar(255),
    machine_id varchar(255)
);

alter table public.transition
    owner to postgres;

create table if not exists public.transition_actions
(
    actions_id                   bigint not null
        constraint fk_transition_actions_a
            references public.action,
    jpa_repository_transition_id bigint not null
        constraint fk_transition_actions_t
            references public.transition,
    primary key (actions_id, jpa_repository_transition_id)
);

alter table public.transition_actions
    owner to postgres;

