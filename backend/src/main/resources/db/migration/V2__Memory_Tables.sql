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
  id           varchar(50) not null primary key,
  created_at   timestamp,
  content      CLOB,
  system       varchar(50),
  model        varchar(50),
  assistant_id varchar(50),
  embeddings   CLOB,
  foreign key (assistant_id) references assistant (id)
);

create index idx_memory_document_system on memory_document (system);
create index idx_memory_document_model on memory_document (model);
create index idx_memory_document_assistantId on memory_document (assistant_id);
create index idx_memory_document_created_at on memory_document (created_at);

