<!--
  - Copyright (c) 2023-2024 Jean Schmitz.
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col cols="8">
          <v-form>
            <v-text-field v-model="newContentText" label="New content"></v-text-field>
          </v-form>
        </v-col>
        <v-col>
          <v-form>
            <v-btn @click="onAddNewContent">Add content</v-btn>
          </v-form>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="8">
          <v-form>
            <v-text-field v-model="searchText" label="Similarity search"></v-text-field>
          </v-form>
        </v-col>
        <v-col cols="2">
          <v-form>
            <v-text-field v-model="searchThreshold"
                          hint="A double value ranging from 0 to 1, where values closer to 1 indicate higher similarity. By default, if you set a threshold of 0.75, for instance, only documents with a similarity above this value are returned"
                          label="Threshold"></v-text-field>
          </v-form>
        </v-col>
        <v-col cols="1">
          <v-btn @click="onSearchMemory">Search</v-btn>
        </v-col>
        <v-col cols="1">
          <v-btn @click="onShowAllMemory">Show all</v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-data-table v-model="selected" :items="memoryList" item-value="ID"
                        show-select></v-data-table>
        </v-col>
      </v-row>
    </v-container>
  </v-app>
</template>

<script>
import {onMounted, ref} from 'vue';
import {useMemory} from '@/composable/use-memory';

export default {
  setup() {
    const memory = useMemory();

    const newContentText = ref('');
    const searchText = ref('');
    const memoryList = ref([]);
    const selected = ref([]);
    const searchThreshold = ref(0.5);

    onMounted(async () => {
      //await onShowAllMemory();
    });

    const onAddNewContent = async () => {
      await memory.store(newContentText.value);
      //await onShowAllMemory();
    };

    const onSearchMemory = async () => {
      let memoryResponses = await memory.search(searchText.value, 10, searchThreshold.value);
      updateTable(memoryResponses);
    };

    const onShowAllMemory = async () => {
      let memoryResponses = await memory.search('', 10, 0.0);
      updateTable(memoryResponses);
    };

    function updateTable(memoryResponses) {
      memoryList.value = [];
      memoryResponses.map((memory) => {
        memoryList.value.push({
          'ID': memory.id,
          'Content': memory.content,
        });
      });
    }

    return {
      searchText,
      newContentText,
      memoryList,
      selected,
      onAddNewContent,
      onShowAllMemory,
      onSearchMemory,
      searchThreshold,
    };
  },
};

</script>

<style scoped>

</style>
