<template>
  <div class="container mx-auto py-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold tracking-tight">Аварии</h1>
      <p class="text-gray-500 mt-1">Управление авариями и аномалиями</p>
    </div>

    <!-- Filters -->
    <div class="mb-6">
      <AccidentFilters @apply="applyFilters" @reset="resetFilters" />
    </div>

    <!-- Stats Summary -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <Card>
        <CardContent class="p-4">
          <div class="text-2xl font-bold">{{ stats.totalAccidents }}</div>
          <div class="text-sm text-gray-500">Всего аварий</div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-4">
          <div class="text-2xl font-bold text-blue-500">
            {{ stats.accidentsByProbability.LOW || 0 }}
          </div>
          <div class="text-sm text-gray-500">Низкая вероятность</div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-4">
          <div class="text-2xl font-bold text-yellow-500">
            {{ stats.accidentsByProbability.MEDIUM || 0 }}
          </div>
          <div class="text-sm text-gray-500">Средняя вероятность</div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-4">
          <div class="text-2xl font-bold text-red-500">
            {{
              stats.accidentsByProbability.HIGH ||
              0 + (stats.accidentsByProbability.CRITICAL || 0)
            }}
          </div>
          <div class="text-sm text-gray-500">
            Высокая/Критическая вероятность
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Table -->
    <Card>
      <CardHeader>
        <CardTitle>Список аварий</CardTitle>
      </CardHeader>
      <CardContent>
        <AccidentTable
          :accidents="accidents"
          :loading="loading"
          @view="handleView"
        />

        <!-- Pagination -->
        <div class="mt-6 flex items-center justify-between">
          <div class="text-sm text-gray-500">
            Показано {{ Math.min(pageSize, accidents.length) }} из
            {{ totalElements }} записей
          </div>
          <div class="flex items-center space-x-2">
            <Button
              :disabled="currentPage === 1 || loading"
              @click="changePage(currentPage - 1)"
              variant="outline"
              size="sm"
            >
              Назад
            </Button>

            <div class="flex space-x-1">
              <Button
                v-for="page in getPageNumbers()"
                :key="page"
                :disabled="loading"
                @click="changePage(page)"
                :variant="page === currentPage ? 'default' : 'outline'"
                size="sm"
              >
                {{ page }}
              </Button>
            </div>

            <Button
              :disabled="currentPage === totalPages || loading"
              @click="changePage(currentPage + 1)"
              variant="outline"
              size="sm"
            >
              Вперед
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import AccidentTable from "@/components/AccidentTable.vue";
import AccidentFilters from "@/components/AccidentFilters.vue";
import { accidentApi } from "@/api/accident";
import type {
  AccidentResponse,
  PageAccidentResponse,
  ProbabilityType,
} from "@/api/types";

// State
const accidents = ref<AccidentResponse[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

// Stats
const stats = reactive({
  totalAccidents: 0,
  accidentsByProbability: {
    LOW: 0,
    MEDIUM: 0,
    HIGH: 0,
    CRITICAL: 0,
  },
});

// Filters
const filters = reactive({
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

// Fetch data
const fetchData = async () => {
  loading.value = true;

  try {
    // Make API call based on active filters
    let response: PageAccidentResponse;

    // If we have ITP number filter, we need to handle it specially
    // For now, we'll use a more generic approach that can be extended
    const pageable = {
      page: currentPage.value - 1,
      size: pageSize.value,
    };

    // If we have date range
    if (filters.startDate && filters.endDate) {
      response = await accidentApi.getByDateRangePaged(
        filters.startDate,
        filters.endDate,
        pageable,
      );
    }
    // If we have probability type
    else if (filters.probabilityType) {
      response = await accidentApi.getByProbabilityPaged(
        filters.probabilityType as ProbabilityType,
        pageable,
      );
    }
    // Check for anomaly filters
    else if (
      filters.isGvsFirstChannelFlowAnomaly !== undefined ||
      filters.isGvsSecondChannelFlowAnomaly !== undefined ||
      filters.isHvsConsumptionFlowAnomaly !== undefined ||
      filters.isHvsGvsConsumptionFlowsAnomaly !== undefined ||
      filters.isGvsChannelsFlowsRatioAnomaly !== undefined ||
      filters.isGvsChannelsFlowsNegativeRatioAnomaly !== undefined
    ) {
      // For anomaly filters, fetch all with anomalies
      const anomalyFilters = {
        isGvsFirstChannelFlowAnomaly: filters.isGvsFirstChannelFlowAnomaly,
        isGvsSecondChannelFlowAnomaly: filters.isGvsSecondChannelFlowAnomaly,
        isHvsConsumptionFlowAnomaly: filters.isHvsConsumptionFlowAnomaly,
        isHvsGvsConsumptionFlowsAnomaly:
          filters.isHvsGvsConsumptionFlowsAnomaly,
        isGvsChannelsFlowsRatioAnomaly: filters.isGvsChannelsFlowsRatioAnomaly,
        isGvsChannelsFlowsNegativeRatioAnomaly:
          filters.isGvsChannelsFlowsNegativeRatioAnomaly,
      };

      // For pagination with anomaly filters, we'd need a specific endpoint
      // Using getAllWithAnomalies and manually handling pagination for now
      const allAccidents =
        await accidentApi.getAllWithAnomalies(anomalyFilters);

      // Apply manual pagination
      const startIndex = (currentPage.value - 1) * pageSize.value;
      const endIndex = startIndex + pageSize.value;
      const paginatedContent = allAccidents.slice(startIndex, endIndex);

      response = {
        content: paginatedContent,
        totalElements: allAccidents.length,
        totalPages: Math.ceil(allAccidents.length / pageSize.value),
        first: currentPage.value === 1,
        last:
          currentPage.value === Math.ceil(allAccidents.length / pageSize.value),
        number: currentPage.value - 1,
        numberOfElements: paginatedContent.length,
        size: pageSize.value,
        empty: allAccidents.length === 0,
        sort: { empty: true, sorted: false, unsorted: true },
        pageable: {
          sort: { empty: true, sorted: false, unsorted: true },
          offset: startIndex,
          paged: true,
          pageNumber: currentPage.value - 1,
          pageSize: pageSize.value,
          unpaged: false,
        },
      };
    }
    // Default case
    else {
      response = await accidentApi.getAllPaged(pageable);
    }

    accidents.value = response.content;
    totalElements.value = response.totalElements;
    totalPages.value = response.totalPages;
  } catch (error) {
    console.error("Error fetching accidents:", error);
    // Handle error appropriately
  } finally {
    loading.value = false;
  }
};

// Fetch statistics
const fetchStats = async () => {
  try {
    // For now, we're using mock data or a general endpoint
    // You might need to implement a proper endpoint for this based on filters
    const allAccidents = await accidentApi.getAll();

    // Calculate stats
    stats.totalAccidents = allAccidents.length;

    // Count by probability type
    stats.accidentsByProbability.LOW = allAccidents.filter(
      (a) => a.probabilityType === "LOW",
    ).length;
    stats.accidentsByProbability.MEDIUM = allAccidents.filter(
      (a) => a.probabilityType === "MEDIUM",
    ).length;
    stats.accidentsByProbability.HIGH = allAccidents.filter(
      (a) => a.probabilityType === "HIGH",
    ).length;
    stats.accidentsByProbability.CRITICAL = allAccidents.filter(
      (a) => a.probabilityType === "CRITICAL",
    ).length;
  } catch (error) {
    console.error("Error fetching statistics:", error);
  }
};

// Page navigation
const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchData();
  }
};

// Generate page numbers for pagination
const getPageNumbers = () => {
  const pages = [];
  const startPage = Math.max(1, currentPage.value - 2);
  const endPage = Math.min(totalPages.value, currentPage.value + 2);

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return pages;
};

// Filter handling
const applyFilters = (newFilters: any) => {
  Object.assign(filters, newFilters);
  currentPage.value = 1; // Reset to first page when filters change
  fetchData();
  fetchStats();
};

const resetFilters = () => {
  // Reset all filters
  Object.keys(filters).forEach((key) => {
    (filters as any)[key] = undefined;
  });
  currentPage.value = 1;
  fetchData();
  fetchStats();
};

// View accident details
const handleView = (accident: AccidentResponse) => {
  console.log("View accident:", accident);
  // Here you would typically navigate to a detail page
};

// Initialize data
onMounted(() => {
  fetchData();
  fetchStats();
});
</script>
