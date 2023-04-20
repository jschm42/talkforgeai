const GOOGLE_SEARCH_API_KEY = process.env.GOOGLE_SEARCH_API_KEY;
const GOOGLE_SEARCH_ENGINE_ID = process.env.GOOGLE_SEARCH_ENGINE_ID;
const GOOGLE_SEARCH_URL = `https://www.googleapis.com/customsearch/v1?key=${GOOGLE_SEARCH_API_KEY};&cx=${GOOGLE_SEARCH_ENGINE_ID}`;

class GoogleSearchService {
  async query(query: string) {
    const response = await fetch(`${GOOGLE_SEARCH_URL}&q=${query}`);
    return response.json();
  }
}

export default GoogleSearchService;
