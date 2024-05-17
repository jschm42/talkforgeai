/*
 * Copyright (c) 2023-2024 Jean Schmitz.
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

create table memory_document
(
  id         varchar(50) not null primary key,
  created_at timestamp,
  content    CLOB,
  embeddings CLOB
);

create table memory_metadata
(
  id                 varchar(50) not null primary key,
  memory_document_id varchar(50) not null,
  metadata_key       varchar(50) not null,
  metadata_value     CLOB,
  foreign key (memory_document_id) references memory_document (id)
);

create index idx_memory_document_content on memory_document (content);
create index idx_memory_document_created_at on memory_document (created_at);

create index idx_memory_metadata_key on memory_metadata (metadata_key);
create index idx_memory_metadata_memory_document_id on memory_metadata (memory_document_id);
