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

}

export default PersonaService;
