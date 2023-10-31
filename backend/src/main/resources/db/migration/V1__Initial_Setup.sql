/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create table chat_message
(
    id                      blob         not null primary key,
    content                 CLOB,
    created_at              timestamp,
    function_call_arguments CLOB,
    function_call_name      varchar(255),
    function_name           varchar(255),
    modified_at             timestamp,
    role                    varchar(255) not null,
    type                    varchar(255) not null,
    chat_session_id         blob         not null,
    check (role in ('SYSTEM', 'USER', 'ASSISTANT', 'FUNCTION')),
    check (type in ('UNPROCESSED', 'PROCESSED', 'FUNCTION_CALL'))
);

create table chat_session
(
    id          blob         not null primary key,
    created_at  timestamp,
    description varchar(256) not null,
    modified_at timestamp,
    title       varchar(256) not null,
    persona_id  blob
);

create table persona
(
    id                blob         not null primary key,
    created_at        timestamp,
    description       varchar(256) not null,
    image_path        varchar(128),
    modified_at       timestamp,
    name              varchar(32)  not null unique,
    request_functions blob,
    background        CLOB,
    personality       CLOB
);

create table persona_properties
(
    persona_id     blob         not null,
    property_value varchar(255),
    property_key   varchar(255) not null,
    primary key (persona_id, property_key)
);
