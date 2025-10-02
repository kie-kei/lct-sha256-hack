<template>
  <div class="space-y-6 p-6">
    <div>
      <h1 class="text-3xl font-bold tracking-tight">Статистика</h1>
      <p class="text-muted-foreground">Анализ данных по ИТП</p>
    </div>

    <Card>
      <CardHeader>
        <CardTitle>Фильтры</CardTitle>
      </CardHeader>
      <CardContent class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <Label for="itp-select">ИТП</Label>
          <Select v-model="selectedItpId">
            <SelectTrigger>
              <SelectValue placeholder="Выберите ИТП" />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                <SelectItem v-for="itp in itps" :key="itp.id" :value="itp.id">
                  {{ itp.number }}
                </SelectItem>
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>

        <div>
          <Label for="start-date">Дата начала</Label>
          <Input id="start-date" v-model="startDate" type="datetime-local" />
        </div>

        <div>
          <Label for="end-date">Дата окончания</Label>
          <Input id="end-date" v-model="endDate" type="datetime-local" />
        </div>

        <div>
          <Label for="time-step">Шаг времени</Label>
          <Select v-model="timeStep">
            <SelectTrigger>
              <SelectValue placeholder="Выберите шаг" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="HOUR">Час</SelectItem>
              <SelectItem value="DAY">День</SelectItem>
              <SelectItem value="WEEK">Неделя</SelectItem>
              <SelectItem value="MONTH">Месяц</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </CardContent>
      <CardFooter>
        <Button @click="fetchStatistics" :disabled="!isFormValid">
          Применить фильтры
        </Button>
      </CardFooter>
    </Card>

    <div v-if="loading" class="flex justify-center items-center h-64">
      <div class="text-center">
        <div
          class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"
        ></div>
        <p class="mt-2">Загрузка данных...</p>
      </div>
    </div>

    <div
      v-else-if="error"
      class="bg-destructive/10 border border-destructive text-destructive p-4 rounded-md"
    >
      {{ error }}
    </div>

    <div v-else-if="statistics" class="space-y-6">
      <!-- Difference Chart -->
      <Card>
        <CardHeader>
          <CardTitle>Разность расходов</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="h-96">
            <FlowDifferenceChart
              :data="statistics.flowDifferences"
              :loading="loading"
            />
          </div>
        </CardContent>
      </Card>

      <!-- GVS and HVS Charts -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- GVS Chart -->
        <Card>
          <CardHeader>
            <CardTitle>ГВС</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="h-80">
              <GvsFlowChart
                :data="statistics.gvsFlowGraph"
                :loading="loading"
              />
            </div>
          </CardContent>
        </Card>

        <!-- HVS Chart -->
        <Card>
          <CardHeader>
            <CardTitle>ХВС</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="h-80">
              <HvsFlowChart
                :data="statistics.hvsFlowGraph"
                :loading="loading"
              />
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Combined GVS and HVS Chart -->
      <Card>
        <CardHeader>
          <CardTitle>ГВС и ХВС</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="h-96">
            <CombinedFlowChart
              :gvs-data="statistics.gvsFlowGraph"
              :hvs-data="statistics.hvsFlowGraph"
              :loading="loading"
            />
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { itpApi } from "@/api/itp";
import { statisticsApi } from "@/api/statistics";
import type {
  UUID,
  TimeStep,
  ITPResponse,
  StatisticResponse,
} from "@/api/types";
import type { PageITPResponse } from "@/api/types";

import FlowDifferenceChart from "@/components/charts/FlowDifferenceChart.vue";
import GvsFlowChart from "@/components/charts/GvsFlowChart.vue";
import HvsFlowChart from "@/components/charts/HvsFlowChart.vue";
import CombinedFlowChart from "@/components/charts/CombinedFlowChart.vue";

// Form state
const selectedItpId = ref<UUID | null>(null);
const startDate = ref<string>("");
const endDate = ref<string>("");
const timeStep = ref<TimeStep>("DAY");

// Data state
const itps = ref<ITPResponse[]>([]);
const statistics = ref<StatisticResponse | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

// Validation
const isFormValid = computed(() => {
  return (
    selectedItpId.value && startDate.value && endDate.value && timeStep.value
  );
});

// Fetch ITPs
const fetchItps = async () => {
  try {
    loading.value = true;
    const pageable = { page: 0, size: 100 }; // Get all ITPs
    const response: PageITPResponse = await itpApi.getAll(pageable);
    itps.value = response.content;
  } catch (err) {
    console.error("Error fetching ITPs:", err);
    error.value = "Ошибка при загрузке ИТП";
  } finally {
    loading.value = false;
  }
};

// Fetch statistics
const fetchStatistics = async () => {
  if (!isFormValid.value) return;

  try {
    loading.value = true;
    error.value = null;

    const formattedStart = startDate.value.replace("T", " ");
    const formattedEnd = endDate.value.replace("T", " ");

    statistics.value = await statisticsApi.getByITP(
      selectedItpId.value!,
      formattedStart,
      formattedEnd,
      timeStep.value,
    );
  } catch (err) {
    console.error("Error fetching statistics:", err);
    error.value = "Ошибка при загрузке статистики";
  } finally {
    loading.value = false;
  }
};

// Initialize
onMounted(async () => {
  await fetchItps();

  // Set default dates to last week
  const now = new Date();
  const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);

  startDate.value = weekAgo.toISOString().slice(0, 16);
  endDate.value = now.toISOString().slice(0, 16);
});
</script>
