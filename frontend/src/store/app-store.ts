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

import {defineStore} from 'pinia';
import ErrorMessage from '@/store/to/error-message';

export const useAppStore = defineStore('app', {
  state: () => {
    return {
      isErrorState: false,
      errors: [] as Array<ErrorMessage>,
    };
  },
  actions: {
    hasErrorState() {
      return this.isErrorState;
    },
    resetErrorState() {
      this.isErrorState = false;
      this.errors = [];
    },
    hasErrors() {
      return this.errors.length > 0;
    },
    addError(errorMessage: string) {
      const error = new ErrorMessage();
      error.id = new Date().getTime().toString();
      error.message = errorMessage;
      this.errors.push(error);
      this.isErrorState = true;
    },
    clearErrors() {
      this.errors = [];
    },
    handleError(error: any) {
      console.log('HANDLE ERROR', error);
      if (error.response) {
        // Request made and server responded
        console.error('Error response: ', error.response.data.message);
        console.error('Error status: ', error.response.status);
        console.error('Error headers: ', error.response.headers);
        this.addError(error.response.data.message);
      } else if (error.request) {
        // The request was made but no response was received
        console.error('Error request without response: ', error.request);
        const responseText = error.request.response || '';
        this.addError('Error on request. ' + responseText);
      } else {
        // Something happened in setting up the request that triggered an Error
        console.error('Error: ', error.message);
        const errorText = error.message || '';
        this.addError('Error. ' + errorText);
      }
    },
  },
});
