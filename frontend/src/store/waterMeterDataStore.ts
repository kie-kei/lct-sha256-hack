import type { UUID, WaterMeterDataResponse } from "@/api/types";
import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useWaterMeterDataStore = defineStore("waterMeterData", () => {
  const items = ref<WaterMeterDataResponse[]>([]);

  const getAll = computed(() => items.value);

  const getById = (id: UUID) => {
    return items.value.find((item) => item.id === id);
  };

  const getByItpId = (itpId: UUID): WaterMeterDataResponse[] => {
    return items.value.filter((item) => item.itpId === itpId);
  };

  const add = (item: WaterMeterDataResponse) => {
    if (!items.value.some((existing) => existing.id === item.id)) {
      items.value.push({ ...item });
    }
  };

  const addMany = (newItems: WaterMeterDataResponse[]) => {
    const uniqueNewItems = newItems.filter(
      (newItem) => !items.value.some((existing) => existing.id === newItem.id),
    );
    items.value.push(...uniqueNewItems.map((item) => ({ ...item })));
  };

  const update = (id: UUID, updateData: Partial<WaterMeterDataResponse>) => {
    const index = items.value.findIndex((item) => item.id === id);
    if (index !== -1) {
      items.value[index] = {
        ...items.value[index],
        ...updateData,
      } as WaterMeterDataResponse;
    }
  };

  const remove = (id: UUID) => {
    const index = items.value.findIndex((item) => item.id === id);
    if (index !== -1) {
      items.value.splice(index, 1);
    }
  };

  const clear = () => {
    items.value = [];
  };

  return {
    items,

    getAll,
    getById,
    getByItpId,

    add,
    addMany,
    update,
    remove,
    clear,
  };
});
