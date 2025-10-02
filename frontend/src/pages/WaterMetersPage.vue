<template>
  <div class="p-6 max-w-7xl mx-auto">
    <div class="mb-8 flex flex-row gap-8">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">Водяные счетчики</h1>
        <p class="text-muted-foreground">
          Просмотр и фильтрация данных с водяных счетчиков
        </p>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-row gap-4 w-full mb-6">
      <div class="flex-1 p-4 bg-card rounded-lg border">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- ITP Selector -->
          <div>
            <label class="text-sm font-medium mb-1 block">ИТП</label>
            <Select v-model="selectedItpId">
              <SelectTrigger>
                <SelectValue placeholder="Выберите ИТП" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="itp in itps" :key="itp.id" :value="itp.id">
                  {{ itp.number }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>

          <!-- Date Range Filter -->
          <div>
            <label class="text-sm font-medium mb-1 block">Начальная дата</label>
            <Input
              type="date"
              v-model="startDate"
              placeholder="Начальная дата"
            />
          </div>

          <div>
            <label class="text-sm font-medium mb-1 block">Конечная дата</label>
            <Input type="date" v-model="endDate" placeholder="Конечная дата" />
          </div>
        </div>

        <div class="mt-4 flex gap-2">
          <Button @click="applyFilters">Применить фильтры</Button>
          <Button variant="outline" @click="resetFilters">Сбросить</Button>
        </div>
      </div>
      <Card>
        <CardContent class="flex flex-col gap-2">
          <h2>Загрузка данных из эксель</h2>
          <div class="flex flex-col gap-2">
            <label class="text-sm font-medium mb-1 block">ИТП</label>
            <Select v-model="selectedItpIdForUpload">
              <SelectTrigger>
                <SelectValue placeholder="Выберите ИТП" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="itp in itps" :key="itp.id" :value="itp.id">
                  {{ itp.number }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex flex-col gap-2">
            <Label>Файл ГВС</Label>
            <Input
              @change="onGvsFileChange"
              type="file"
              accept=".xlsx,.xls,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            />
          </div>
          <div class="flex flex-col gap-2">
            <Label>Файл ХВС</Label>
            <Input
              @change="onHvsFileChange"
              type="file"
              accept=".xlsx,.xls,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            />
          </div>
        </CardContent>
        <CardFooter>
          <Button @click="handleUploadExcel" class="cursor-pointer"
            >Загрузить</Button
          >
        </CardFooter>
      </Card>
    </div>

    <!-- Water Meter Table -->
    <Card>
      <CardHeader>
        <CardTitle>Данные водяных счетчиков</CardTitle>
        <CardDescription>
          Таблица с данными по водяным счетчикам
        </CardDescription>
      </CardHeader>
      <CardContent>
        <WaterMeterTable
          v-if="selectedItpId"
          :itpId="selectedItpId"
          :totalPages="totalPages"
          :totalItems="totalItems"
          :pageable="pageable"
          :data="waterMeterData"
          v-model="pageable"
          @page-changed="onPageChange"
        />
        <div
          v-else
          class="flex items-center justify-center p-8 text-muted-foreground"
        >
          Пожалуйста, выберите ИТП для отображения данных
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import WaterMeterTable from "@/components/custom/WaterMeterTable.vue";
import { itpApi } from "@/api/itp";
import { waterMeterDataApi } from "@/api/waterMeterData";
import type {
  ITPResponse,
  Pageable,
  PageITPResponse,
  PageWaterMeterDataResponse,
  UUID,
} from "@/api/types";
import { Label } from "@/components/ui/label";
import CardFooter from "@/components/ui/card/CardFooter.vue";

import { toast } from "vue-sonner";
// State
const itps = ref<ITPResponse[]>([]);
const waterMeterData = ref<
  {
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
    measurementTimestamp: string;
    createdAt: string;
    updatedAt: string;
    number: string;
  }[]
>([]);
const selectedItpId = ref<string>("");
const startDate = ref<string>("");
const endDate = ref<string>("");
const totalPages = ref<number>(0);
const totalItems = ref<number>(0);

const selectedItpIdForUpload = ref<string>("");
const excelHvs = ref<File | null>(null);
const excelGvs = ref<File | null>(null);

const handleUploadExcel = async () => {
  if (selectedItpIdForUpload.value === "") {
    toast.warning("Для загрузки заполнены не все поля", {
      description: "Выберите ИТП для которого загружаются данные",
    });
    return;
  }
  if (excelGvs.value === null) {
    toast.warning("Для загрузки заполнены не все поля", {
      description: "Выберите файл для ГВС",
    });
    return;
  }
  if (excelHvs.value === null) {
    toast.warning("Для загрузки заполнены не все поля", {
      description: "Выберите файл для ХВС",
    });
    return;
  }
  const response = await waterMeterDataApi.uploadWaterMeterData(
    selectedItpIdForUpload.value,
    excelGvs.value,
    excelHvs.value,
  );
  waterMeterData.value = [
    ...waterMeterData.value,
    ...response.map((x) => {
      return {
        ...x,
        createdAt: new Date(x.createdAt).toDateString(),
        number: itps.value.find((x) => x.id === selectedItpIdForUpload.value)
          ?.number!,
      };
    }),
  ];
  toast.info("Загрузка успешна", {
    description: "Эксель файлы загружены",
  });
};

const onGvsFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  excelGvs.value = input.files?.[0] || null;
};
const onHvsFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  excelHvs.value = input.files?.[0] || null;
};
// Pagination state
const pageable = ref<Pageable>({
  page: 0,
  size: 15,
  sort: ["createdAt,desc"],
});

// Fetch ITPs
const fetchItps = async () => {
  try {
    const response: PageITPResponse = await itpApi.getAll({
      page: 0,
      size: 100, // Get all ITPs
    });
    itps.value = response.content;
  } catch (error) {
    console.error("Error fetching ITPs:", error);
  }
};

// Fetch water meter data
const fetchWaterMeterData = async () => {
  if (!selectedItpId.value) {
    waterMeterData.value = [];
    totalPages.value = 0;
    totalItems.value = 0;
    return;
  }

  try {
    let response: PageWaterMeterDataResponse;

    response = await waterMeterDataApi.getByITPPaged(
      selectedItpId.value,
      pageable.value,
    );

    waterMeterData.value = response.content.map((x) => {
      return {
        ...x,
        createdAt: new Date(x.createdAt).toDateString(),
        number: itps.value.find((x) => x.id === selectedItpId.value)?.number!,
      };
    });
    totalPages.value = response.totalPages;
    totalItems.value = response.totalElements;
  } catch (error) {
    console.error("Error fetching water meter data:", error);
    waterMeterData.value = [];
    totalPages.value = 0;
    totalItems.value = 0;
  }
};

// Apply filters
const applyFilters = () => {
  // Reset to first page when applying filters
  pageable.value.page = 0;
  fetchWaterMeterData();
};

// Reset filters
const resetFilters = () => {
  selectedItpId.value = "";
  startDate.value = "";
  endDate.value = "";
  pageable.value.page = 0;
  fetchWaterMeterData();
};

// Handle page change
const onPageChange = (page: number) => {
  pageable.value.page = page - 1; // Convert to 0-indexed
  fetchWaterMeterData();
};

// Watch for changes in selected ITP or pagination
watch(selectedItpId, () => {
  pageable.value.page = 0; // Reset to first page when ITP changes
  fetchWaterMeterData();
});

// Initialize
onMounted(() => {
  fetchItps();
  fetchWaterMeterData();
});
</script>
