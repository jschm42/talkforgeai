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
  <v-container class="scrollable-container">
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
                    v-model="selectedAssistants"
                    v-model:items-per-page="serverTable.itemsPerPage"
                    :headers="serverTable.headers"
                    :items="serverTable.serverItems"
                    :items-length="serverTable.totalItems"
                    :loading="serverTable.loading"
                    :search="searchModifier"
                    item-value="id"
                    show-select
                    @update:options="loadServerItems">
                  <template v-slot:tfoot>
                    <tr>
                      <td>
                      </td>
                      <td>
                        <v-text-field v-model="searchContent" class="ma-2" density="compact"
                                      hide-details placeholder="Search content..."></v-text-field>
                      </td>
                      <td>
                        <v-text-field
                            v-model="searchAssistantName"
                            class="ma-2"
                            density="compact"
                            hide-details
                            placeholder="Search Assistant..."
                            type="string"
                        ></v-text-field>
                      </td>
                      <td>
                        <v-select v-model="searchMessageType"
                                  :items="['', 'ASSISTANT', 'USER', 'SYSTEM']"
                                  class="ma-2"
                                  density="compact" hide-details
                                  placeholder="Search Message Type..."></v-select>
                      </td>
                      <td>
                        <v-select v-model="searchSystem" :items="availableSystems" class="ma-2"
                                  density="compact" hide-details
                                  placeholder="Search System..."></v-select>
                      </td>
                    </tr>
                  </template>


                </v-data-table-server>
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-form>
                  <v-btn class="m-2" variant="tonal" @click="showDeleteItemsModal=true">
                    Delete selected
                  </v-btn>
                  <v-btn class="m-2" variant="tonal" @click="showDeleteAllModal=true">
                    Delete all
                  </v-btn>
                  Total items: {{ serverTable.totalItems }}
                </v-form>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="6">
                <v-form>
                  <v-text-field v-model="newContentText" label="New content"></v-text-field>
                </v-form>
              </v-col>
              <v-col cols="2">
                <v-form>
                  <v-select v-model="selectedAssistant" :disabled="isGlobalContent"
                            :item-props="itemProps"
                            :items="availableAssistants" label="Assistant"></v-select>
                </v-form>
              </v-col>
              <v-col cols="2">
                <v-form>
                  <v-checkbox v-model="isGlobalContent" label="Global"
                              @change="onChangeIsGlobalContent"></v-checkbox>
                </v-form>
              </v-col>
              <v-col cols="2">
                <v-form>
                  <v-btn variant="tonal" @click="onAddNewContent">Add content</v-btn>
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

  <QuestionModal
      :isOpen="showDeleteItemsModal"
      message="'Are you sure you want to delete the selected memory entries?'"
      title="Delete memory entries"
      @answer="onDeleteMemoryEntriesAnswer"
  />

  <QuestionModal
      :isOpen="showDeleteAllModal"
      message="'Are you sure you want to delete all memory entries?'"
      title="Delete all memory entries"
      @answer="onDeleteAllAnswer"
  />

</template>

<script>
import {onMounted, ref, watch} from 'vue';
import {useMemory} from '@/composable/use-memory';
import {useAssistants} from '@/composable/use-assistants';
import QuestionModal from '@/components/common/QuestionModal.vue';
import {useAppStore} from '@/store/app-store';

export default {
  components: {QuestionModal},
  setup() {
    const memory = useMemory();
    const assistants = useAssistants();
    const appStore = useAppStore();

    const tab = ref(null);
    const similarityTable = ref([]);

    const serverTable = ref({
      itemsPerPage: 10,
      headers: [
        {title: 'Content', key: 'content', align: 'start', sortable: true, width: '40%'},
        {title: 'Assistant', key: 'assistantName', align: 'start', sortable: true},
        {title: 'Type', key: 'messageType', align: 'start', sortable: true},
        {title: 'System', key: 'system', align: 'start', sortable: true},
        {title: 'Model', key: 'model', align: 'start', sortable: true},
      ],
      serverItems: [],
      loading: true,
      totalItems: 0,
    });

    const showDeleteItemsModal = ref(false);
    const showDeleteAllModal = ref(false);
    const isGlobalContent = ref(true);
    const searchContent = ref('');
    const selectedAssistant = ref('');
    const selectedAssistants = ref([]);
    const availableAssistants = ref([]);
    const availableSystems = ref([]);
    const searchAssistantName = ref('');
    const searchText = ref('');
    const searchSystem = ref('ALL');
    const searchMessageType = ref('');
    let searchModifier = ref('');
    const newContentText = ref('');
    const selected = ref([]);
    const searchThreshold = ref(0.5);

    onMounted(async () => {
      availableAssistants.value = await assistants.retrieveAssistants();
      const systems = await assistants.retrieveLlmSystems();
      availableSystems.value = ['ALL', ...systems.map((system) => system.key)];
      await loadServerItems({page: 1, itemsPerPage: serverTable.value.itemsPerPage});
    });

    watch(searchContent, async () => {
      searchModifier.value = String(Date.now());
    });

    watch(searchAssistantName, async () => {
      searchModifier.value = String(Date.now());
    });

    watch(searchMessageType, async () => {
      searchModifier.value = String(Date.now());
    });

    watch(searchSystem, async () => {
      searchModifier.value = String(Date.now());
    });

    const loadServerItems = async (pageable) => {
      console.log('Loading items', pageable);
      serverTable.value.loading = true;

      try {
        const resultList = await memory.list(pageable.page, pageable.itemsPerPage,
            pageable.sortBy, {
              content: searchContent.value,
              assistantName: searchAssistantName.value,
              system: searchSystem.value === 'ALL' ? '' : searchSystem.value,
              messageType: searchMessageType.value,
            });

        resultList.forEach((item) => {
          // shorten the content if it is too long
          if (item.content.length > 200) {
            item.content = item.content.substring(0, 200) + '...';
          }
        });

        serverTable.value.serverItems = resultList;
        serverTable.value.totalItems = await memory.count();
        serverTable.value.loading = false;
      } catch (error) {
        console.error('Error loading items', error);
        this.appStore.handleError(error);
        serverTable.value.loading = false;
      }
    };

    const itemProps = (item) => {
      return {
        title: item.name,
        subtitle: item.description,
      };
    };

    const onChangeIsGlobalContent = () => {
      if (isGlobalContent.value) {
        selectedAssistant.value = '';
      }
    };

    const onAddNewContent = async () => {
      try {
        await memory.store(newContentText.value, selectedAssistant.value.id);
        await loadServerItems({page: 1, itemsPerPage: serverTable.value.itemsPerPage});
      } catch (error) {
        console.error('Error adding new content', error);
        appStore.handleError(error);
      }
    };

    const onSearchSimilarity = async () => {
      try {
        let memoryResponses = await memory.search(searchText.value, 10, searchThreshold.value);
        updateSimilarityTable(memoryResponses);
      } catch (error) {
        console.error('Error searching similarity', error);
        appStore.handleError(error);
      }
    };

    const onClearSimilarities = async () => {
      similarityTable.value = [];
    };

    const onDeleteMemoryEntriesAnswer = async (answer) => {
      showDeleteItemsModal.value = false;
      try {
        if (answer) {
          await onDeleteSelected();
        }
      } catch (error) {
        console.error('Error deleting memory entries', error);
        appStore.handleError(error);
      }
    };

    const onDeleteAllAnswer = async (answer) => {
      showDeleteAllModal.value = false;
      try {
        if (answer) {
          await onDeleteAll();
        }
      } catch (error) {
        console.error('Error deleting all memory entries', error);
        appStore.handleError(error);
      }
    };

    const onDeleteSelected = async () => {
      try {
        await memory.remove(selectedAssistants.value);
        await loadServerItems({page: 1, itemsPerPage: serverTable.value.itemsPerPage});
      } catch (error) {
        console.error('Error deleting selected memory entries', error);
        appStore.handleError(error);
      }
    };

    const onDeleteAll = async () => {
      try {
        await memory.clear();
        await loadServerItems({page: 1, itemsPerPage: serverTable.value.itemsPerPage});
      } catch (error) {
        console.error('Error deleting all memory entries', error);
        appStore.handleError(error);
      }
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
      showDeleteItemsModal,
      showDeleteAllModal,
      isGlobalContent,
      searchText,
      newContentText,
      selectedAssistant,
      selectedAssistants,
      availableAssistants,
      availableSystems,
      selected,
      itemProps,
      onAddNewContent,
      onSearchSimilarity,
      onClearSimilarities,
      onDeleteSelected,
      onDeleteAll,
      onDeleteMemoryEntriesAnswer,
      onChangeIsGlobalContent,
      onDeleteAllAnswer,
      searchThreshold,
      serverTable,
      loadServerItems,
      updateSimilarityTable,
      similarityTable,
      tab,
      searchModifier,
      searchContent,
      searchAssistantName,
      searchSystem,
      searchMessageType,
      appStore,
    };
  },
};

</script>

<style scoped>
.scrollable-container {
  overflow-y: auto;
  height: 100vh; /* Adjust this value as needed */
}

</style>
