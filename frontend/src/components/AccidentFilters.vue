<template>
  <Card>
    <CardHeader>
      <CardTitle>Фильтры</CardTitle>
    </CardHeader>
    <CardContent>
      <form
        @submit.prevent="onSubmit"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"
      >
        <!-- ITP Number Filter -->
        <div class="space-y-2">
          <Label for="itpNumber">Номер ИТП</Label>
          <Input
            id="itpNumber"
            v-model="filters.itpNumber"
            placeholder="Введите номер ИТП"
            @input="onInput"
          />
        </div>

        <!-- Probability Filter -->
        <div class="space-y-2">
          <Label for="probabilityType">Вероятность</Label>
          <Select
            v-model="filters.probabilityType"
            @update:modelValue="onInput"
          >
            <SelectTrigger>
              <SelectValue placeholder="Выберите вероятность" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="LOW">Низкая</SelectItem>
              <SelectItem value="MEDIUM">Средняя</SelectItem>
              <SelectItem value="HIGH">Высокая</SelectItem>
              <SelectItem value="CRITICAL">Критическая</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <!-- Date Range Filters -->
        <div class="space-y-2">
          <Label for="startDate">Начальная дата</Label>
          <Input
            id="startDate"
            v-model="filters.startDate"
            type="datetime-local"
            @input="onInput"
          />
        </div>

        <div class="space-y-2">
          <Label for="endDate">Конечная дата</Label>
          <Input
            id="endDate"
            v-model="filters.endDate"
            type="datetime-local"
            @input="onInput"
          />
        </div>

        <!-- Anomaly Filters -->
        <div class="space-y-2">
          <Label>Аномалии</Label>
          <div class="flex flex-wrap gap-2">
            <div class="flex items-center space-x-2">
              <Checkbox
                id="gvsFirstChannelFlowAnomaly"
                :checked="filters.isGvsFirstChannelFlowAnomaly"
                @update:checked="
                  updateFilter('isGvsFirstChannelFlowAnomaly', $event)
                "
              />
              <Label for="gvsFirstChannelFlowAnomaly" class="text-sm"
                >1 канал ГВС</Label
              >
            </div>
          </div>
          <div class="flex flex-wrap gap-2">
            <div class="flex items-center space-x-2">
              <Checkbox
                id="gvsSecondChannelFlowAnomaly"
                :checked="filters.isGvsSecondChannelFlowAnomaly"
                @update:checked="
                  updateFilter('isGvsSecondChannelFlowAnomaly', $event)
                "
              />
              <Label for="gvsSecondChannelFlowAnomaly" class="text-sm"
                >2 канал ГВС</Label
              >
            </div>
          </div>
          <div class="flex flex-wrap gap-2">
            <div class="flex items-center space-x-2">
              <Checkbox
                id="hvsConsumptionFlowAnomaly"
                :checked="filters.isHvsConsumptionFlowAnomaly"
                @update:checked="
                  updateFilter('isHvsConsumptionFlowAnomaly', $event)
                "
              />
              <Label for="hvsConsumptionFlowAnomaly" class="text-sm"
                >Потребление ХВС</Label
              >
            </div>
          </div>
          <div class="flex flex-wrap gap-2">
            <div class="flex items-center space-x-2">
              <Checkbox
                id="hvsGvsConsumptionFlowsAnomaly"
                :checked="filters.isHvsGvsConsumptionFlowsAnomaly"
                @update:checked="
                  updateFilter('isHvsGvsConsumptionFlowsAnomaly', $event)
                "
              />
              <Label for="hvsGvsConsumptionFlowsAnomaly" class="text-sm"
                >Потоки ХВС/ГВС</Label
              >
            </div>
          </div>
          <div class="flex flex-wrap gap-2">
            <div class="flex items-center space-x-2">
              <Checkbox
                id="gvsChannelsFlowsRatioAnomaly"
                :checked="filters.isGvsChannelsFlowsRatioAnomaly"
                @update:checked="
                  updateFilter('isGvsChannelsFlowsRatioAnomaly', $event)
                "
              />
              <Label for="gvsChannelsFlowsRatioAnomaly" class="text-sm"
                >Соотношение каналов ГВС</Label
              >
            </div>
          </div>
          <div class="flex flex-wrap gap-2">
            <div class="flex items-center space-x-2">
              <Checkbox
                id="gvsChannelsFlowsNegativeRatioAnomaly"
                :checked="filters.isGvsChannelsFlowsNegativeRatioAnomaly"
                @update:checked="
                  updateFilter('isGvsChannelsFlowsNegativeRatioAnomaly', $event)
                "
              />
              <Label for="gvsChannelsFlowsNegativeRatioAnomaly" class="text-sm"
                >Отриц. соотн. каналов ГВС</Label
              >
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex items-end space-x-2">
          <Button type="submit" variant="default">Применить</Button>
          <Button type="button" variant="outline" @click="onReset"
            >Сбросить</Button
          >
        </div>
      </form>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import type { ProbabilityType } from "@/api/types";

// Define filter interface
interface Filters {
  itpNumber?: string;
  probabilityType?: ProbabilityType;
  startDate?: string;
  endDate?: string;
  isGvsFirstChannelFlowAnomaly?: boolean;
  isGvsSecondChannelFlowAnomaly?: boolean;
  isHvsConsumptionFlowAnomaly?: boolean;
  isHvsGvsConsumptionFlowsAnomaly?: boolean;
  isGvsChannelsFlowsRatioAnomaly?: boolean;
  isGvsChannelsFlowsNegativeRatioAnomaly?: boolean;
}

// Initialize filters
const filters = reactive<Filters>({
  itpNumber: undefined,
  probabilityType: undefined,
  startDate: undefined,
  endDate: undefined,
  isGvsFirstChannelFlowAnomaly: undefined,
  isGvsSecondChannelFlowAnomaly: undefined,
  isHvsConsumptionFlowAnomaly: undefined,
  isHvsGvsConsumptionFlowsAnomaly: undefined,
  isGvsChannelsFlowsRatioAnomaly: undefined,
  isGvsChannelsFlowsNegativeRatioAnomaly: undefined,
});

// Define emits
const emit = defineEmits<{
  apply: [filters: Filters];
  reset: [];
}>();

// Handle input changes (emit immediately)
const onInput = () => {
  emit("apply", { ...filters });
};

// Handle filter updates with proper boolean handling
const updateFilter = (key: keyof Filters, value: boolean | undefined) => {
  // Toggle the value if it's currently undefined or false
  // This allows the user to cycle between: undefined -> true -> false -> undefined
  if (value === true) {
    (filters as any)[key] = true;
  } else if (value === false) {
    (filters as any)[key] = false;
  } else {
    (filters as any)[key] = undefined;
  }
  onInput();
};

// Handle form submission
const onSubmit = () => {
  emit("apply", { ...filters });
};

// Handle reset
const onReset = () => {
  // Reset all filters to undefined
  for (const key in filters) {
    (filters as any)[key] = undefined;
  }
  emit("reset");
};
</script>
