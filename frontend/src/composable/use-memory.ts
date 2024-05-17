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
import {useChatStore} from '@/store/chat-store';

export class MemoryResponse {
  id = '';
  content = '';
  assistantId = '';
  system = '';
  model = '';
}

export function useMemory() {
  const chatStore = useChatStore();

  const store = async (content: string, assistantId?: string): Promise<MemoryResponse> => {
    const result = await axios.post('/api/v1/memory/store', {
      content,
      assistantId,
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

  const list = async (
      page: number, pageSize: number, sortBy: []): Promise<Array<MemoryResponse>> => {
    const result = await axios.post(
        `/api/v1/memory/list`, {
          page,
          pageSize,
          sortBy,
        });

    return result.data;
  };

  const remove = async (memoryIds: string[]) => {
    await axios.post('/api/v1/memory/remove', {
      memoryIds,
    });
  };

  const clear = async () => {
    await axios.post('/api/v1/memory/clear');
  };

  const count = async (): Promise<number> => {
    const result = await axios.get('/api/v1/memory/count');
    return result.data;
  };

  return {
    store,
    search,
    list,
    count,
    remove,
    clear,
  };
}