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
        <h1>Memory</h1>
      </v-row>
      <v-row>
        <v-col>
          <v-data-table-server
              v-model:items-per-page="serverTable.itemsPerPage"
              :headers="serverTable.headers"
              :items="serverTable.serverItems"
              :items-length="serverTable.totalItems"
              :loading="serverTable.loading"
              :search="serverTable.search"
              item-value="content"
              @update:options="loadServerItems"
          ></v-data-table-server>
        </v-col>
      </v-row>
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
      <v-divider></v-divider>
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
          <v-btn @click="onSearchSimilarity">Search</v-btn>
        </v-col>
        <v-col cols="1">
          <v-btn @click="onClearSimilarities">Clear</v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-data-table :items="similarityTable">
        </v-data-table>
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

    const similarityTable = ref([]);

    const serverTable = ref({
      itemsPerPage: 5,
      headers: [
        {title: 'Content', key: 'content', align: 'start', sortable: false},
        {title: 'ID', key: 'id', align: 'start', sortable: false},
      ],
      search: '',
      serverItems: [],
      loading: true,
      totalItems: 0,
    });

    const newContentText = ref('');
    const searchText = ref('');
    const selected = ref([]);
    const searchThreshold = ref(0.5);

    onMounted(async () => {
      await loadServerItems({page: 1, itemsPerPage: 10});
    });

    const loadServerItems = async (page) => {
      console.log('Loading items', page);
      serverTable.value.loading = true;
      serverTable.value.serverItems = await memory.list(page.page, page.itemsPerPage);
      serverTable.value.totalItems = await memory.count();
      serverTable.value.loading = false;
    };

    const onAddNewContent = async () => {
      await memory.store(newContentText.value);
      await loadServerItems();
    };

    const onSearchSimilarity = async () => {
      let memoryResponses = await memory.search(searchText.value, 10, searchThreshold.value);
      updateSimilarityTable(memoryResponses);
    };

    const onClearSimilarities = async () => {
      similarityTable.value = [];
    };

    const updateSimilarityTable = (similarityResponses) => {
      similarityTable.value = [];
      similarityResponses.map((similarity) => {
        similarityTable.value.push({
          'Content': similarity.content,
          'ID': similarity.id,
        });
      });
    };

    return {
      searchText,
      newContentText,
      selected,
      onAddNewContent,
      onSearchSimilarity,
      onClearSimilarities,
      searchThreshold,
      serverTable,
      loadServerItems,
      updateSimilarityTable,
      similarityTable,
    };
  },
};

</script>

<style scoped>

</style>
