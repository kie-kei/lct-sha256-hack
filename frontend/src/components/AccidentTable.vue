<template>
  <div class="w-full overflow-auto">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead class="w-[100px]">ИТП</TableHead>
          <TableHead class="w-[180px]">Дата и время</TableHead>
          <TableHead class="w-[120px]">Вероятность</TableHead>
          <TableHead class="w-[300px]">Аномалии</TableHead>
          <TableHead class="w-[300px]">Значения</TableHead>
          <TableHead class="w-[180px]">Создано</TableHead>
          <TableHead class="w-[100px]">Действия</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody v-if="accidents.length > 0">
        <TableRow
          v-for="accident in accidents"
          :key="accident.id"
          class="hover:bg-gray-50"
        >
          <TableCell class="font-medium">{{ accident.itpNumber }}</TableCell>
          <TableCell>{{ formatDate(accident.measurementTimestamp) }}</TableCell>
          <TableCell>
            <span :class="getProbabilityClass(accident.probabilityType)">
              {{ accident.probabilityType }}
            </span>
          </TableCell>
          <TableCell>
            <div class="flex flex-wrap gap-1">
              <span
                v-if="accident.isGvsFirstChannelFlowAnomaly"
                class="inline-flex items-center rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-800"
              >
                1 канал ГВС
              </span>
              <span
                v-if="accident.isGvsSecondChannelFlowAnomaly"
                class="inline-flex items-center rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-800"
              >
                2 канал ГВС
              </span>
              <span
                v-if="accident.isHvsConsumptionFlowAnomaly"
                class="inline-flex items-center rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-800"
              >
                Потребление ХВС
              </span>
              <span
                v-if="accident.isHvsGvsConsumptionFlowsAnomaly"
                class="inline-flex items-center rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-800"
              >
                Потоки ХВС/ГВС
              </span>
              <span
                v-if="accident.isGvsChannelsFlowsRatioAnomaly"
                class="inline-flex items-center rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-800"
              >
                Соотношение каналов ГВС
              </span>
              <span
                v-if="accident.isGvsChannelsFlowsNegativeRatioAnomaly"
                class="inline-flex items-center rounded-full bg-red-100 px-2 py-1 text-xs font-medium text-red-800"
              >
                Отриц. соотн. каналов ГВС
              </span>
            </div>
          </TableCell>
          <TableCell>
            <div class="flex flex-col gap-1 max-w-[300px]">
              <div
                v-if="accident.isGvsFirstChannelFlowAnomaly"
                class="flex flex-wrap items-center gap-2 text-xs"
              >
                <span class="font-medium">1 канал ГВС:</span>
                <span class="bg-blue-50 px-1 rounded"
                  >Факт: {{ accident.gvsActualFirstChannelFlowValue }}</span
                >
                <span class="bg-green-50 px-1 rounded"
                  >Станд: {{ accident.gvsStandardFirstChannelFlowValue }}</span
                >
              </div>
              <div
                v-if="accident.isGvsSecondChannelFlowAnomaly"
                class="flex flex-wrap items-center gap-2 text-xs"
              >
                <span class="font-medium">2 канал ГВС:</span>
                <span class="bg-blue-50 px-1 rounded"
                  >Факт: {{ accident.gvsActualSecondChannelFlowValue }}</span
                >
                <span class="bg-green-50 px-1 rounded"
                  >Станд: {{ accident.gvsStandardSecondChannelFlowValue }}</span
                >
              </div>
              <div
                v-if="accident.isHvsConsumptionFlowAnomaly"
                class="flex flex-wrap items-center gap-2 text-xs"
              >
                <span class="font-medium">Потребление ХВС:</span>
                <span class="bg-blue-50 px-1 rounded"
                  >Факт: {{ accident.hvsActualConsumptionFlowValue }}</span
                >
                <span class="bg-green-50 px-1 rounded"
                  >Станд: {{ accident.hvsStandardConsumptionFlowValue }}</span
                >
              </div>
              <div
                v-if="accident.isHvsGvsConsumptionFlowsAnomaly"
                class="flex flex-wrap items-center gap-2 text-xs"
              >
                <span class="font-medium">Дельта ХВС/ГВС:</span>
                <span class="bg-blue-50 px-1 rounded"
                  >Дельта: {{ accident.hvsGvsConsumptionFlowsDelta }}</span
                >
              </div>
              <div
                v-if="accident.isGvsChannelsFlowsRatioAnomaly"
                class="flex flex-wrap items-center gap-2 text-xs"
              >
                <span class="font-medium">Соотн. каналов ГВС:</span>
                <span class="bg-blue-50 px-1 rounded"
                  >Соотн: {{ accident.gvsChannelsFlowsRatio }}</span
                >
              </div>
              <div
                v-if="accident.isGvsChannelsFlowsNegativeRatioAnomaly"
                class="flex flex-wrap items-center gap-2 text-xs"
              >
                <span class="font-medium">Отриц. соотн.:</span>
                <span class="bg-blue-50 px-1 rounded"
                  >Соотн: {{ accident.gvsChannelsFlowsNegativeRatio }}</span
                >
              </div>
            </div>
          </TableCell>
          <TableCell>{{ formatDate(accident.createdAt) }}</TableCell>
          <TableCell>
            <Button variant="outline" size="sm" @click="onView(accident)">
              Подробнее
            </Button>
          </TableCell>
        </TableRow>
      </TableBody>
      <TableBody v-else>
        <TableRow>
          <TableCell :colspan="7" class="text-center py-8">
            <div class="flex flex-col items-center justify-center">
              <p class="text-gray-500">Нет данных</p>
              <p class="text-gray-400 text-sm mt-1" v-if="loading">
                Загрузка...
              </p>
            </div>
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </div>
</template>

<script setup lang="ts">
import type { AccidentResponse, ProbabilityType } from "@/api/types";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";

// Define props
interface Props {
  accidents: AccidentResponse[];
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
});

// Define emits
const emit = defineEmits<{
  view: [accident: AccidentResponse];
}>();

// Format date
const formatDate = (dateString: string) => {
  try {
    const date = new Date(dateString);
    return date.toLocaleString("ru-RU");
  } catch {
    return dateString;
  }
};

// Get probability type class for styling
const getProbabilityClass = (probabilityType: ProbabilityType) => {
  const baseClass =
    "inline-flex items-center rounded-full px-2 py-1 text-xs font-medium";

  switch (probabilityType) {
    case "LOW":
      return `${baseClass} bg-blue-100 text-blue-800`;
    case "MEDIUM":
      return `${baseClass} bg-yellow-100 text-yellow-800`;
    case "HIGH":
      return `${baseClass} bg-orange-100 text-orange-800`;
    case "CRITICAL":
      return `${baseClass} bg-red-100 text-red-800`;
    default:
      return `${baseClass} bg-gray-100 text-gray-800`;
  }
};

// View handler
const onView = (accident: AccidentResponse) => {
  emit("view", accident);
};
</script>
