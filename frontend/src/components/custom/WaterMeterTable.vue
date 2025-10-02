<script setup lang="ts">
import { computed } from "vue";
import PaginationTable from "@/components/custom/PaginationTable.vue";
import { type Pageable, type UUID } from "@/api/types";
const props = defineProps<{
  itpId: string;
  totalPages: number;
  totalItems: number;
  pageable: Pageable;
  data: {
    id: UUID;
    itpId: UUID;
    heatMeterIdentifier?: UUID;
    firstChannelFlowmeterIdentifier?: UUID;
    secondChannelFlowmeterIdentifier?: UUID;
    gvsFirstChannelFlowValue?: number;
    gvsSecondChannelFlowValue?: number;
    gvsConsumptionFlowValue?: number;
    waterMeterIdentifier?: UUID;
    hvsFlowValue?: number;
    measurementTimestamp?: string;
    createdAt?: string;
    updatedAt?: string;
    number?: string;
  };
}>();
const pageable = defineModel<Pageable>({ required: true });
const currentPage = computed(() => pageable.value.page + 1);
const columns = [
  { key: "createdAt", label: "Дата" },
  { key: "number", label: "Номер ИТП" },
  { key: "gvsConsumptionFlowValue", label: "Потребление на ГВС" },
  { key: "gvsFirstChannelFlowValue", label: "Приход на ГВС" },
  { key: "gvsSecondChannelFlowValue", label: "Возврат на ГВС" },
  { key: "hvsFlowValue", label: "Поток на ХВС" },
];

const emits = defineEmits(["page-changed"]);
const handlePageChange = (page: number) => {
  console.log("handlePageChange", page);
  if (page < 1 || page > props.totalPages) return;
  pageable.value = { ...pageable.value, page: page - 1 };
  emits("page-changed", page);
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
