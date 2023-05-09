/**
 * Shim file for Typescript. Needed for VUE-Modules
 */
declare module '*.vue' {
  import {DefineComponent} from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}
