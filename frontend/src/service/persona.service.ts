/*
 * Copyright (c) 2023 Jean Schmitz.
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

import axios from 'axios';
import Persona from '@/store/to/persona';

class PersonaService {

  async readAllPersona(): Promise<Array<Persona>> {
    try {
      const response = await axios.get('/api/v1/persona');
      return response.data;
    } catch (error) {
      console.error('Error reading persona: ', error);
    }
    return [];
  }

  async readPersona(personaId: string): Promise<Persona> {
    try {
      const response = await axios.get('/api/v1/persona/' + personaId);
      return response.data;
    } catch (error) {
      console.error('Error reading persona: ', error);
    }
    return new Persona();
  }

  async writePersona(persona: Persona) {
    try {
      return await axios.post('/api/v1/persona', persona);
    } catch (error) {
      console.error('Error writing persona: ', error);
    }
  }

  async generatePersonaImage(prompt: string) {
    try {
      return await axios.post(`/api/v1/persona/image/generate`, {prompt});
    } catch (error) {
      console.error('Error generating persona image: ', error);
    }
  }

}

export default PersonaService;
