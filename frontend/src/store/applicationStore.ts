import { defineStore } from "pinia";
import { ref } from "vue";

export const useApplicationStore = defineStore("application", () => {
  const secondLayer = ref<string>("");
  return {
    secondLayer,
  };
});
