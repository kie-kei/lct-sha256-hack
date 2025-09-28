import type { ITPResponse, UUID } from "@/api/types";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useItpStore = defineStore("itp", () => {
  const itp = ref<ITPResponse[]>([]);

  const getAll = () => itp.value;

  const getById = (id: UUID): ITPResponse | undefined => {
    return itp.value.find((item) => item.id === id);
  };

  const add = (item: ITPResponse): void => {
    if (!getById(item.id)) {
      itp.value.push(item);
    }
  };

  const addMany = (items: ITPResponse[]): void => {
    const existingIds = new Set(itp.value.map((i) => i.id));
    const uniqueItems = items.filter((item) => !existingIds.has(item.id));
    itp.value.push(...uniqueItems);
  };

  const update = (item: ITPResponse): void => {
    const index = itp.value.findIndex((i) => i.id === item.id);
    if (index !== -1) {
      itp.value[index] = item;
    } else {
    }
  };

  const remove = (id: UUID): void => {
    itp.value = itp.value.filter((item) => item.id !== id);
  };

  const clear = (): void => {
    itp.value = [];
  };

  return {
    itp,
    getAll,
    getById,
    add,
    addMany,
    update,
    remove,
    clear,
  };
});
