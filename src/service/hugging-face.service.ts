import fs from 'fs';
import {HfInference} from '@huggingface/inference';

const HUGGING_FACE_API_URL = 'https://api-inference.huggingface.co';
const HF_API_TOKEN = process.env.HUGGING_FACE_API_KEY;

enum Models {
  BART_LARGE_CNN = 'facebook/bart-large-cnn',
  T5_BASE = 't5-base',
  VIT_GPT2_IMAGE_CAPTIONING = 'nlpconnect/vit-gpt2-image-captioning',
  BLIP_IMAGE_CAP_BASE = 'Salesforce/blip-image-captioning-base',
  TROCR = 'microsoft/trocr-base-printed',
}

class HuggingFaceService {

  #inference;

  constructor() {
    this.#inference = new HfInference(HF_API_TOKEN);
  }

  async translate(text: string) {
    return this.query(Models.T5_BASE, text);
  }

  async imageClassification(filename: string) {
    return this.queryFile(Models.BLIP_IMAGE_CAP_BASE, filename);
  }

  async textImageToText(filename: string) {
    return this.queryFile(Models.TROCR, filename);
  }

  async queryFile(model: string, filename: string) {
    if (!fs.existsSync(filename)) {
      throw new Error(`File ${filename} does not exist`);
    }

    const data = fs.readFileSync(filename);

    const response = await fetch(
        `${HUGGING_FACE_API_URL}/models/${model}`,
        {
          headers: {Authorization: `Bearer ${HF_API_TOKEN}`},
          method: 'POST',
          body: data,
        },
    );
    const result = await response.json();
    console.log('Response from Hugging Face', result);
    return result;
  }

  async query(model: string, data: string) {
    console.log('Requesting Model from Hugging Face', model);
    const config = {
      inputs: data,
      options: {
        wait_for_model: true,
      },
    };

    const response = await fetch(
        `${HUGGING_FACE_API_URL}/models/${model}`,
        {
          headers: {Authorization: `Bearer ${HF_API_TOKEN}`},
          method: 'POST',
          body: JSON.stringify(config),
        },
    );
    const result = await response.json();
    console.log('Response from Hugging Face', result);
    return result;
  }

}

export default HuggingFaceService;