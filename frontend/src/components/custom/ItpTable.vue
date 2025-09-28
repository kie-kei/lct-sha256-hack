<script setup lang="ts">
import { useApplicationStore } from "@/store/applicationStore";
import { computed } from "vue";

import PaginationTable from "@/components/custom/PaginationTable.vue";
import type { Pageable } from "@/api/types";
import { useRouter } from "vue-router";
const { pageable, itp } = defineProps<{
  pageable: Pageable;
  totalPages: number;
  totalItems: number;
  itp: {
    mkdId: string;
    id: string;
    number: string;
    address: string;
    unom: string;
  }[];
}>();
const currentPage = computed(() => pageable.page + 1);
const columns = [
  { key: "number", label: "Номер ИТП" },
  { key: "address", label: "Адрес МКД" },
  { key: "unom", label: "УНОМ МКД" },
];
const data = computed(() => itp);
const emits = defineEmits(["page-changed"]);
const handlePageChange = (page: number) => {
  emits("page-changed", page);
};
const applicationStore = useApplicationStore();
const router = useRouter();
const handleRowClick = async (item: Record<string, any>) => {
  const number = item["number"];
  const id = item["id"];
  applicationStore.secondLayer = number;
  router.push(`/itp/${id}/water_meter`);
};
</script>

<template>
  <PaginationTable
    :data="data"
    :columns="columns"
    :items-per-page="pageable.size"
    :default-page="1"
    :current-page="currentPage"
    :total-pages="totalPages"
    :total-items="totalItems"
    @update:page="(x) => handlePageChange(x)"
    @row-click="handleRowClick"
  />
</template>
