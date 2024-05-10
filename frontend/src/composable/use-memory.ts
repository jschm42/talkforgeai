/*
 * Copyright (c) 2024 Jean Schmitz.
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

/**
 * This module provides a set of functions to manage and interact with chat assistants.
 * It includes functions to handle streaming, synchronization, selection, retrieval, deletion, and creation of assistants.
 * It also includes functions to manage threads and messages, including retrieval, creation, deletion, and updating.
 * Additionally, it provides utility functions for handling assistant images and prompts.
 *
 * @module useAssistants
 */
import axios from 'axios';

export class MemoryResponse {
  id = '';
  content = '';
}

export function useMemory() {

  const store = async (content: string): Promise<MemoryResponse> => {
    const result = await axios.post('/api/v1/memory/store', {
      content,
    });
    return result.data;
  };

  const search = async (
      query: string, topK = 4, similarityThreshold = 0.75): Promise<Array<MemoryResponse>> => {
    const result = await axios.post('/api/v1/memory/search', {
      query,
      topK,
      similarityThreshold,
    });
    return result.data;
  };

  const list = async (): Promise<Array<MemoryResponse>> => {
    const result = await axios.get('/api/v1/memory/list');
    return result.data;
  };

  return {
    store,
    search,
    list,
  };
}