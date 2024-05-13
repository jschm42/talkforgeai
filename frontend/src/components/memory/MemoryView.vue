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

      <v-card>
        <v-tabs
            v-model="tab"
            bg-color="primary">
          <v-tab value="one">Memory</v-tab>
          <v-tab value="two">Similarity Search</v-tab>
        </v-tabs>

        <v-card-text>
          <v-tabs-window v-model="tab">
            <v-tabs-window-item value="one">
              <v-row>
                <v-col>
                  <v-data-table-server
                      v-model:items-per-page="serverTable.itemsPerPage"
                      :headers="serverTable.headers"
                      :items="serverTable.serverItems"
                      :items-length="serverTable.totalItems"
                      :loading="serverTable.loading"
                      :search="searchModifier"
                      item-value="content"
                      @update:options="loadServerItems"
                  >
                    <template v-slot:tfoot>
                      <tr>
                        <td>
                          <v-text-field v-model="searchContent" class="ma-2" density="compact"
                                        hide-details placeholder="Search content..."></v-text-field>
                        </td>
                        <td>
                          <v-text-field
                              v-model="searchId"
                              class="ma-2"
                              density="compact"
                              hide-details
                              placeholder="Search ID..."
                              type="string"
                          ></v-text-field>
                        </td>
                      </tr>
                    </template>


                  </v-data-table-server>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="9">
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
            </v-tabs-window-item>

            <v-tabs-window-item value="two">
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
                <v-data-table :items="similarityTable" hide-default-footer>
                </v-data-table>
              </v-row>

            </v-tabs-window-item>
          </v-tabs-window>
        </v-card-text>
      </v-card>

    </v-container>
  </v-app>
</template>

<script>
import {onMounted, ref, watch} from 'vue';
import {useMemory} from '@/composable/use-memory';

export default {
  setup() {
    const memory = useMemory();

    const tab = ref(null);
    const similarityTable = ref([]);

    const serverTable = ref({
      itemsPerPage: 10,
      headers: [
        {title: 'Content', key: 'content', align: 'start', sortable: true},
        {title: 'ID', key: 'id', align: 'start', sortable: false},
      ],
      serverItems: [],
      loading: true,
      totalItems: 0,
    });

    const searchContent = ref('');
    const searchId = ref('');
    let searchModifier = ref('');
    const newContentText = ref('');
    const searchText = ref('');
    const selected = ref([]);
    const searchThreshold = ref(0.5);

    onMounted(async () => {
      await loadServerItems({page: 1, itemsPerPage: serverTable.value.itemsPerPage});
    });

    watch(searchContent, async (newContent, oldContent) => {
      console.log('Search content', newContent);
      //searchModifier = String(Date.now());
      await loadServerItems(
          {page: 1, itemsPerPage: serverTable.value.itemsPerPage, search: newContent});
    });

    const loadServerItems = async (pageable) => {
      console.log('Loading items', pageable);
      serverTable.value.loading = true;
      serverTable.value.serverItems = await memory.list(pageable.page, pageable.itemsPerPage,
          pageable.sortBy, pageable.search);
      serverTable.value.totalItems = await memory.count();
      serverTable.value.loading = false;
    };

    const onAddNewContent = async () => {
      await memory.store(newContentText.value);
      await loadServerItems({page: 1, itemsPerPage: serverTable.value.itemsPerPage});
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
      tab,
      searchModifier,
      searchContent,
      searchId,
    };
  },
};

</script>

<style scoped>

</style>
