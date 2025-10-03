<template>
  <WaterMeterTable
    :itpId="uuid"
    v-model="pageable"
    :data="waterMeterData"
    :total-items="totalItems"
    :total-pages="totalPages"
    @page-changed="() => loadWaterMeterList()"
  />
</template>
<script setup lang="ts">
import { itpApi } from "@/api/itp";
import type { Pageable, PageWaterMeterDataResponse, UUID } from "@/api/types";
import { waterMeterDataApi } from "@/api/waterMeterData";
import WaterMeterTable from "@/components/custom/WaterMeterTable.vue";
import { ref } from "vue";
import { onMounted } from "vue";

const { uuid } = defineProps<{
  uuid: string;
}>();
const waterMeterData = ref<
  {
    id: UUID;
    itpId: UUID;
    heatMeterIdentifier: UUID;
    firstChannelFlowmeterIdentifier: UUID;
    secondChannelFlowmeterIdentifier: UUID;
    gvsFirstChannelFlowValue: number;
    gvsSecondChannelFlowValue: number;
    gvsConsumptionFlowValue: number;
    waterMeterIdentifier: UUID;
    hvsFlowValue: number;
    measurementTimestamp: string;
    createdAt: string;
    updatedAt: string;
    number: string;
  }[]
>([]);

onMounted(async () => {
  await loadWaterMeterList();
});

const totalPages = ref<number>(0);
const totalItems = ref<number>(0);
const pageable = ref<Pageable>({
  page: 0,
  size: 15,
  sort: ["id", "asc"],
});
const loadWaterMeterList = async () => {
  try {
    const response: PageWaterMeterDataResponse =
      await waterMeterDataApi.getByITPPaged(uuid, pageable.value);
    const itpResponse = await itpApi.getById(uuid);
    waterMeterData.value = response.content.map((x) => {
      return {
        ...x,
        number: itpResponse.number,
        createdAt: new Date(x.createdAt).toDateString(),
      };
    });
    totalPages.value = response.totalPages;
    totalItems.value = response.totalElements;
  } catch (err) {
    console.error("Ошибка при загрузке ITP:", err);
  }
};
</script>
