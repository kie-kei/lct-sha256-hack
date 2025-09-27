<script setup lang="ts">
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
const props = defineProps<{
  data: Array<{
    itpNumber: string;
    mkdAddress: string;
  }>;
}>();
</script>

<template>
  <Table>
    <TableCaption>A list of your recent invoices.</TableCaption>
    <TableHeader>
      <TableRow>
        <TableHead>Номер ИТП</TableHead>
        <TableHead>Адресс МКД</TableHead>
      </TableRow>
    </TableHeader>
    <TableBody>
      <TableRow v-for="item in props.data">
        <TableCell>{{ item.itpNumber }}</TableCell>
        <TableCell>{{ item.mkdAddress }}</TableCell>
      </TableRow>
    </TableBody>
  </Table>
</template>
