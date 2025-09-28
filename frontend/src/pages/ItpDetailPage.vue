<template>
  <WaterMeterTable :itpId="uuid" />
</template>
<script setup lang="ts">
import WaterMeterTable from "@/components/custom/WaterMeterTable.vue";

const { uuid } = defineProps<{
  uuid: string;
}>();
</script>
