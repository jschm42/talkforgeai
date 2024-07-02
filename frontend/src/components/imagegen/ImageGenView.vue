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
  <v-container>
    <v-row>
      <v-col>
        <v-form v-model="valid">
          <v-card>
            <v-tabs
                v-model="tab"
                bg-color="primary">
              <v-tab value="one">Text to Image</v-tab>
            </v-tabs>

            <v-card-text>
              <v-tabs-window v-model="tab">
                <v-tabs-window-item value="one">
                  <v-row>
                    <v-col cols="10">
                      <v-text-field v-model="imagePrompt" label="Image prompt"></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="2">
                      <v-btn @click="onGenerateImage">Generate</v-btn>
                    </v-col>
                  </v-row>
                </v-tabs-window-item>
              </v-tabs-window>
            </v-card-text>
          </v-card>
        </v-form>
      </v-col>
    </v-row>

    <v-row>
      <v-col
          v-for="(item, i) in generatedImages"
          :key="i"
          class="d-flex child-flex"
          cols="4"
      >
        <v-img
            :src="getBase64Src(item.base64Data)"
            aspect-ratio="1"
            class="bg-grey-lighten-2"
        >
          <template v-slot:placeholder>
            <v-row
                align="center"
                class="fill-height ma-0"
                justify="center"
            >
              <v-progress-circular
                  color="grey-lighten-5"
                  indeterminate
              ></v-progress-circular>
            </v-row>
          </template>
        </v-img>
      </v-col>
    </v-row>
  </v-container>

</template>

<script>
import {onMounted, ref} from 'vue';
import {useAssistants} from '@/composable/use-assistants';
import {useAppStore} from '@/store/app-store';
import {useImageGenStore} from '@/store/imagegen-store';
import {storeToRefs} from 'pinia';
import {ImageGenRequest, useImageGen} from '@/composable/use-imagegen';

export default {
  components: {},
  setup() {
    const assistants = useAssistants();
    const appStore = useAppStore();
    const imageGen = useImageGen();
    const imageGenStore = useImageGenStore();
    const {imagePrompt} = storeToRefs(useImageGenStore());
    const valid = ref(false);
    const tab = ref(null);
    const generatedImages = ref([]);

    const getBase64Src = (image) => {
      return `data:image/png;base64,${image}`;
    };

    onMounted(async () => {
    });

    const onGenerateImage = async () => {
      try {
        const request = new ImageGenRequest();
        request.imagePrompt = imagePrompt.value;
        const result = await imageGen.textToImage(request);
        console.log('GENERATED IMAGES', result);

        generatedImages.value = result;
      } catch (error) {
        appStore.handleError(error);
      }
    };

    return {
      valid,
      tab,
      assistants,
      appStore,
      imageGen,
      imageGenStore,
      imagePrompt,
      onGenerateImage,
      generatedImages,
      getBase64Src,
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
