<template>
  <div class="flex flex-col gap-4">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead v-for="column in columns" :key="column.key">
            {{ column.label }}
          </TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <TableRow
          class="hover:brightness-50 cursor-pointer"
          v-for="(item, index) in currentPageData"
          @click="$emit('row-click', item)"
          :key="index"
        >
          <TableCell v-for="column in columns" :key="column.key">
            {{ item[column.key] }}
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>

    <Pagination
      v-if="totalPages > 1"
      :default-page="currentPage"
      :total="totalItems"
      :items-per-page="itemsPerPage"
    >
      <PaginationContent>
        <PaginationPrevious
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
        />

        <template v-for="page in visiblePages" :key="page">
          <PaginationEllipsis v-if="page === -1" />
          <PaginationItem
            class="hover:brightness-75 transition-colors duration-100 cursor-pointer"
            v-else
            :value="page"
            :is-active="page === currentPage"
            @click="handlePageChange(page)"
          >
            {{ page }}
          </PaginationItem>
        </template>

        <PaginationNext
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
        />
      </PaginationContent>
    </Pagination>
  </div>
</template>

<script setup lang="ts">
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";

import { computed, ref, watch } from "vue";

const props = defineProps<{
  data: Record<string, any>[];
  columns: { key: string; label: string }[];
  itemsPerPage?: number;
  currentPage?: number;
  totalPages: number;
  totalItems: number;
}>();

const emit = defineEmits<{
  (e: "update:page", page: number): void;
  (e: "row-click", item: Record<string, any>): void;
}>();

const itemsPerPage = ref(props.itemsPerPage ?? 10);
const currentPage = ref(props.currentPage ?? 1);
const currentPageData = computed(() => {
  return props.data;
  // const start = (currentPage.value - 1) * itemsPerPage.value;
  // const end = start + itemsPerPage.value;
  // return props.data.slice(start, end);
});

watch(
  () => props.currentPage,
  (newPage) => {
    if (newPage !== undefined && newPage !== currentPage.value) {
      currentPage.value = newPage;
    }
  },
  { immediate: true },
);

const handlePageChange = (page: number) => {
  if (page < 1 || page > props.totalPages) {
    console.log("handlePageChange return");
    return;
  }
  currentPage.value = page;
  emit("update:page", page);
};

const visiblePages = computed(() => {
  const total = props.totalPages;
  const current = currentPage.value;
  if (total <= 1) return [];

  const delta = 2;
  const range = [];
  for (
    let i = Math.max(1, current - delta);
    i <= Math.min(total, current + delta);
    i++
  ) {
    range.push(i);
  }

  if (range[0] !== 1) {
    range.unshift(1);
    if (range[1] !== 2) range.unshift(-1);
  }
  if (range[range.length - 1] !== total) {
    if (range[range.length - 1] !== total - 1) range.push(-1);
    range.push(total);
  }

  return range.filter((x, i, arr) => x !== -1 || arr[i - 1] !== -1);
});
</script>
