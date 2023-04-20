const ELEVENLABS_API_KEY = process.env.ELEVENLABS_API_KEY;
const ELEVENLABS_API_URL = 'https://api.elevenlabs.io';
const VOICES = {
  'Rachel': '21m00Tcm4TlvDq8ikWAM',
  'Domi': 'AZnzlk1XvdvUeBnXmlld',
  'Bella': 'EXAVITQu4vr4xnSDxMaL',
  'Antoni': 'ErXwobaYiN019PkySvjV',
  'Elli': 'MF3mGyEYCl7XYWbV9V6O',
  'Josh': 'TxGEqnHWrfWFTfGW9XjX',
  'Arnold': 'VR6AewLTigWG4xSOukaG',
  'Adam': 'pNInz6obpgDQGcFmaJgB',
  'Sam': 'yoZ06aMxZJJ28mfd3POQ',

};

class ElevenlabsService {

  async speachStream(text: string, voiceId: string) {
    const body = {
      text,
      'voice_settings': {
        'stability': 0,
        'similarity_boost': 0,
      },
    };

    const url = `${ELEVENLABS_API_URL}/v1/text-to-speech/${voiceId}/stream`;
    console.log('Elevenlabs URL', url);

    const response = await fetch(
      url,
      {
        // @ts-ignore
        headers: {
          'xi-api-key': ELEVENLABS_API_KEY,
          'Content-Type': 'application/json',
          'accept': '*/*',
        },
        method: 'POST',
        body: JSON.stringify(body),
      },
    );

    if (response.ok) {
      return response.blob();
    } else {
      throw new Error('Error fetching audio stream.');
    }
  }
}

export default ElevenlabsService;

export {
  VOICES,
};
