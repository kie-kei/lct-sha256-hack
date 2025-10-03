<template>
  <div class="h-full flex flex-col relative">
    <MkdMap class="flex-1" @selected="handlePointSelected" :mkd-list="allMkd" />
    <!-- <ItpTable
      :itp="allItp"
      :pageable="pageable"
      :total-items="totalItems"
      :total-pages="totalPages"
      @page-changed="handlePageChange"
    /> -->
    <Card
      v-if="isCardOpen"
      class="absolute right-4 top-4 z-10 shadow-2xl max-h-full overflow-y-auto border border-blue-500"
    >
      <CardHeader
        class="text-start text-lg font-bold flex flex-row justify-between gap-2 items-center"
      >
        Информация о ИТП и МКД
        <Button @click="isCardOpen = false" size="icon" class="cursor-pointer"
          ><X />
        </Button>
      </CardHeader>
      <CardContent class="w-86 h-full flex flex-col gap-4">
        <div class="flex flex-row justify-between">
          <p>Адрес МКД:</p>
          <p>{{ selectedMkdItp?.address }}</p>
        </div>
        <div class="flex flex-row justify-between">
          <p>УНОМ МКД:</p>
          <p>{{ selectedMkdItp?.unom }}</p>
        </div>
        <div class="flex flex-row justify-between">
          <p>Номер ИТП:</p>
          <p>{{ selectedMkdItp?.number }}</p>
        </div>
      </CardContent>
    </Card>
  </div>
</template>
<script setup lang="ts">
import { itpApi } from "@/api/itp";
import { mkdApi } from "@/api/mkd";
import { X } from "lucide-vue-next";
import {
  type ITPResponse,
  type MKDResponse,
  type Pageable,
  type PageITPResponse,
} from "@/api/types";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import MkdMap from "@/components/custom/MkdMap.vue";
import { onMounted } from "vue";
import { ref } from "vue";
import { Button } from "@/components/ui/button";

const selectedMkdItp = ref<{
  number: string | undefined;
  address: string;
  unom: string;
} | null>();

const handlePointSelected = async (ids: string[]) => {
  console.log("ids", ids);
  const selectedId = ids[0];
  if (!selectedId) {
    return;
  }
  const cMkd = allMkd.value.find((x) => x.id === selectedId);
  if (!cMkd) {
    throw Error("ITP not found");
  }
  const response = await itpApi.getById(cMkd.itpId);
  selectedMkdItp.value = { ...response, ...cMkd };
  isCardOpen.value = true;
};

const allMkd = ref<MKDResponse[]>([]);
const allItp = ref<ITPResponse[]>([]);
const itpLoading = ref(false);
const itpError = ref<string | null>(null);
const totalItems = ref(0);
const totalPages = ref(0);
const isCardOpen = ref(false);
const pageable = ref<Pageable>({
  page: 0,
  size: 5,
  sort: ["id", "asc"],
});
const fetchedPage = ref<number[]>([]);
const loadItpList = async () => {
  itpLoading.value = true;
  itpError.value = null;

  try {
    const response: PageITPResponse = await itpApi.getAll(pageable.value);
    if (!fetchedPage.value.includes(pageable.value.page)) {
      fetchedPage.value.push(pageable.value.page);
    }
    response.content.forEach((x) => allItp.value.push(x));
    totalPages.value = response.totalPages;
    totalItems.value = response.totalElements;
    const mkdPromises = allItp.value.map((itp) => {
      return mkdApi.getByItpId(itp.id).catch((err) => {
        console.warn(`MKD для ITP ${itp.id} не найден`, err);
        return null;
      });
    });

    const mkdResults = await Promise.all(mkdPromises);
    (mkdResults.filter(Boolean) as MKDResponse[]).forEach((x) =>
      allMkd.value.push(x),
    );
  } catch (err) {
    console.error("Ошибка при загрузке ITP:", err);
    itpError.value = "Не удалось загрузить данные. Попробуйте позже.";
  } finally {
    itpLoading.value = false;
  }
};

onMounted(() => {
  loadItpList();
});
</script>
