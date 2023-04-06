import {reactive} from 'vue';
import {IndexEntry} from '../service/chat-index.service';
//import {IndexEntry} from '../service/chat-index.service';

const indexStore = reactive({
  entries: [] as Array<IndexEntry>,
  loadIndex(){
    this.entries = window.chatIndexApi.load();
    console.log("Index loaded", this.entries);
  },
  saveIndex(){
    window.chatIndexApi.save(this.entries);
    console.log("Index saved", this.entries);
  },
  addEntry(entry: IndexEntry){
    this.entries.push(entry);
    this.saveIndex();
  }
});

export default indexStore;
