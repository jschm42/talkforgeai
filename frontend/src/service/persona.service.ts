import axios from 'axios';
import Persona from '@/store/to/persona';

class PersonaService {

  async readPersona(): Promise<Array<Persona>> {
    try {
      const response = await axios.get('/api/v1/persona');
      return response.data;
    } catch (error) {
      console.error('Error reading persona: ', error);
    }
    return [];
  }

  async writePersona(persona: Persona) {
    try {
      return await axios.post('/api/v1/persona', persona);
    } catch (error) {
      console.error('Error writing persona: ', error);
    }
  }

}

export default PersonaService;
