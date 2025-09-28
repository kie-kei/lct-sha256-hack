<script setup lang="ts">
import { computed } from "vue";
import { useWaterMeterDataStore } from "@/store/waterMeterDataStore";
import PaginationTable from "@/components/custom/PaginationTable.vue";
import type { Pageable, PageWaterMeterDataResponse } from "@/api/types";
import { ref } from "vue";
import { onMounted } from "vue";
import { waterMeterDataApi } from "@/api/waterMeterData";
// import { useItpStore } from "@/store/itpStore";
const { itpId } = defineProps<{
  itpId: string;
}>();
const waterMeterStore = useWaterMeterDataStore();
// const itpStore = useItpStore();
const totalPages = ref<number>(0);
const totalItems = ref<number>(0);
const loading = ref(false);
const error = ref<string | null>(null);
const pageable = ref<Pageable>({
  page: 0,
  size: 15,
  sort: ["id", "asc"],
});
const currentPage = computed(() => pageable.value.page + 1);
const columns = [
  { key: "gvsConsumptionFlowValue", label: "Потребление на ГВС" },
  { key: "gvsFirstChannelFlowValue", label: "Приход на ГВС" },
  { key: "gvsSecondChannelFlowValue", label: "Возврат на ГВС" },
  { key: "hvsFlowValue", label: "Поток на ХВС" },
];
const data = computed(() => waterMeterStore.getByItpId(itpId));

onMounted(async () => {
  await loadWaterMeterList();
});
const fetchedPage = ref<number[]>([]);
const loadWaterMeterList = async () => {
  loading.value = true;
  error.value = null;

  try {
    const response: PageWaterMeterDataResponse =
      await waterMeterDataApi.getByITPPaged(itpId, pageable.value);
    if (!fetchedPage.value.includes(pageable.value.page)) {
      fetchedPage.value.push(pageable.value.page);
    }
    waterMeterStore.addMany(response.content);
    totalPages.value = response.totalPages;
    totalItems.value = response.totalElements;
  } catch (err) {
    console.error("Ошибка при загрузке ITP:", err);
    error.value = "Не удалось загрузить данные. Попробуйте позже.";
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page: number) => {
  console.log("handlePageChange", page);
  if (page < 1 || page > totalPages.value) return;
  pageable.value = { ...pageable.value, page: page - 1 };
  loadWaterMeterList();
};
</script>

<template>
  <PaginationTable
    :data="data"
    :columns="columns"
    :items-per-page="15"
    :default-page="1"
    :current-page="currentPage"
    :total-pages="totalPages"
    :total-items="totalItems"
    @update:page="(x) => handlePageChange(x)"
  />
</template>
