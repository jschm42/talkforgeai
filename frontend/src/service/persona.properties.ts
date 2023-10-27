const PersonaProperties = {
  TTS_TYPE: 'tts_type',
  SPEECHAPI_VOICE: 'speechAPI_voice',
  ELEVENLABS_VOICEID: 'elevenlabs_voiceId',
  ELEVENLABS_MODELID: 'elevenlabs_modelId',
  ELEVENLABS_SIMILARITYBOOST: 'elevenlabs_similarityBoost',
  ELEVENLABS_STABILITY: 'elevenlabs_stability',
  CHATGPT_MODEL: 'chatgpt_model',
  CHATGPT_TEMPERATURE: 'chatgpt_temperature',
  CHATGPT_TOP_P: 'chatgpt_topP',
  CHATGPT_FREQUENCY_PENALTY: 'chatgpt_frequencyPenalty',
  CHATGPT_PRESENCE_PENALTY: 'chatgpt_presencePenalty',
  FEATURE_PLANTUML: 'feature_plantUMLGeneration',
  FEATURE_IMAGEGENERATION: 'feature_imageGeneration',
  FEATURE_AUTOSPEAKDEFAULT: 'feature_autoSpeakDefault',
  FEATURE_TITLEGENERATION: 'feature_titleGeneration',
};

const TTSType = {
  ELEVENLABS: 'elevenlabs',
  SPEECHAPI: 'speechAPI',
  DISABLED: '',
};

export default PersonaProperties;
export {TTSType};

