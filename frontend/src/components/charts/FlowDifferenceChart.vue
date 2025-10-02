<template>
  <div v-if="loading" class="flex justify-center items-center h-full">
    <div
      class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"
    ></div>
  </div>
  <div v-else-if="chartData.labels.length > 0" class="h-full">
    <Line :data="chartData" :options="chartOptions" :plugins="plugins" />
  </div>
  <div
    v-else
    class="flex justify-center items-center h-full text-muted-foreground"
  >
    Нет данных для отображения
  </div>
</template>

<script setup lang="ts">
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from "chart.js";
import { Line } from "vue-chartjs";
import { computed } from "vue";
import type { FlowDifferenceResponse } from "@/api/types";

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
);

interface FlowDifferenceChartProps {
  data: FlowDifferenceResponse[];
  loading?: boolean;
  plugins?: any[];
}

const props = withDefaults(defineProps<FlowDifferenceChartProps>(), {
  loading: false,
  plugins: () => [],
});

// Format timestamp for display
const formatTimestamp = (timestamp: string): string => {
  const date = new Date(timestamp);
  return date.toLocaleString([], {
    day: "2-digit",
    month: "2-digit",
    year: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// Define the chart data
const chartData = computed(() => {
  if (!props.data || props.data.length === 0) {
    return {
      labels: [],
      datasets: [],
    };
  }

  return {
    labels: props.data.map((item) => formatTimestamp(item.timestamp)),
    datasets: [
      {
        label: "Расход ГВС",
        data: props.data.map((item) => item.gvsConsumption),
        borderColor: "#3b82f6", // blue-500
        backgroundColor: "rgba(59, 130, 246, 0.1)",
        tension: 0.3,
        fill: false,
      },
      {
        label: "Расход ХВС",
        data: props.data.map((item) => item.hvsFlow),
        borderColor: "#10b981", // emerald-500
        backgroundColor: "rgba(16, 185, 129, 0.1)",
        tension: 0.3,
        fill: false,
      },
      {
        label: "Разность",
        data: props.data.map((item) => item.difference),
        borderColor: "#ef4444", // red-500
        backgroundColor: "rgba(239, 68, 68, 0.1)",
        tension: 0.3,
        fill: false,
      },
    ],
  };
});

// Define the chart options
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: "top" as const,
    },
    title: {
      display: true,
      text: "Разность расходов",
    },
    tooltip: {
      mode: "index" as const,
      intersect: false,
    },
  },
  scales: {
    x: {
      display: true,
      title: {
        display: true,
        text: "Время",
      },
    },
    y: {
      display: true,
      title: {
        display: true,
        text: "Значение",
      },
      beginAtZero: false, // Don't force the scale to begin at zero
      min: undefined, // Allow auto-scaling
      suggestedMin: undefined, // Allow auto-scaling
    },
  },
  interaction: {
    mode: "nearest" as const,
    axis: "x" as const,
    intersect: false,
  },
  elements: {
    point: {
      radius: 3,
      hoverRadius: 5,
    },
  },
};
</script>
