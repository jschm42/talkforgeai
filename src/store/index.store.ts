import {reactive} from 'vue';
import IndexEntry from '../service/to/index-entry';

const indexStore = reactive({
  entries: [] as Array<IndexEntry>,
  load() {
    // @ts-ignore
    window.chatIndexAPI.listenToLoadReply((entries: Array<IndexEntry>) => {
      this.entries = [...entries];
      console.log('Index loaded', this.entries);
    });

    // @ts-ignore
    window.chatIndexAPI.load();
  },
  save() {
    // @ts-ignore
    window.chatIndexAPI.save(this.entries);
    console.log('Index saved', this.entries);
  },
  add(entry: IndexEntry) {
    this.entries.push(entry);
    this.save();
  },
});

export default indexStore;
