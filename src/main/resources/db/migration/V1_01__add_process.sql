create table if not exists process
(
    id                bigserial not null primary key,
    process_id        text      not null,
    business_key      text      not null,
    parent_process_id bigint references process (id),
    workflow_id       uuid      not null,
    state             text      not null
);