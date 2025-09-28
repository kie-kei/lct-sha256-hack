import type { MKDResponse, UUID } from "@/api/types";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useMkdStore = defineStore("mkd", () => {
  const mkd = ref<MKDResponse[]>([]);

  const getAll = () => mkd.value;

  const getById = (id: UUID): MKDResponse | undefined => {
    return mkd.value.find((item) => item.id === id);
  };

  const getByItpId = (id: UUID): MKDResponse | undefined => {
    return mkd.value.find((item) => item.itpId === id);
  };

  const add = (item: MKDResponse): void => {
    if (!getById(item.id)) {
      mkd.value.push(item);
    }
  };

  const addMany = (items: MKDResponse[]): void => {
    const existingIds = new Set(mkd.value.map((i) => i.id));
    const uniqueItems = items.filter((item) => !existingIds.has(item.id));
    mkd.value.push(...uniqueItems);
  };

  const update = (item: MKDResponse): void => {
    const index = mkd.value.findIndex((i) => i.id === item.id);
    if (index !== -1) {
      mkd.value[index] = item;
    } else {
      // Опционально: можно выбросить ошибку или добавить как новый
      // Здесь просто игнорируем, т.к. update подразумевает существование
    }
  };

  const remove = (id: UUID): void => {
    mkd.value = mkd.value.filter((item) => item.id !== id);
  };

  const clear = (): void => {
    mkd.value = [];
  };

  return {
    mkd,
    getAll,
    getById,
    add,
    addMany,
    update,
    remove,
    clear,
    getByItpId,
  };
});
