<template>
  <div class="h-full flex flex-col relative">
    <MkdMap
      class="flex-1"
      @selected="handlePointSelected"
      :mkd-list="mkdStore.getAll()"
    />
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
import type { MKDResponse, Pageable, PageITPResponse } from "@/api/types";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardHeader,
  CardFooter,
  CardTitle,
} from "@/components/ui/card";
import MkdMap from "@/components/custom/MkdMap.vue";
import { computed } from "vue";
import { onMounted } from "vue";
import { ref } from "vue";
import { Button } from "@/components/ui/button";
import { useItpStore } from "@/store/itpStore";
import { useMkdStore } from "@/store/mkdStore";
import ItpTable from "@/components/custom/ItpTable.vue";

const selectedMkdItp = ref<{
  number: string;
  address: string;
  unom: string;
} | null>();

const handlePointSelected = (ids: string[]) => {
  console.log("ids", ids);
  const selectedId = ids[0];
  selectedMkdItp.value = data.value.find((x) => x.id === selectedId);
  console.log(
    "data.value.find((x) => x.id === selectedId)",
    data.value.find((x) => x.id === selectedId),
  );
  console.log("selectedMkdItp.value", selectedMkdItp.value);
  isCardOpen.value = true;
};

const itpStore = useItpStore();
const mkdStore = useMkdStore();
const allItp = computed(() => itpStore.getAll());
const itpLoading = ref(false);
const itpError = ref<string | null>(null);
const totalItems = ref(0);
const totalPages = ref(0);
const data = computed(() =>
  itpStore.getAll().map((x) => {
    const mkd = mkdStore.getAll().find((y) => y.itpId === x.id);
    return {
      id: mkd?.id,
      number: x.number,
      address: mkd?.address,
      unom: mkd?.unom,
    };
  }),
);
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
    itpStore.addMany(response.content);
    totalPages.value = response.totalPages;
    totalItems.value = response.totalElements;
    const mkdPromises = itpStore.getAll().map((itp) => {
      const res = mkdStore.getByItpId(itp.id);
      console.log("res", res);
      if (!res) {
        return mkdApi.getByItpId(itp.id).catch((err) => {
          console.warn(`MKD для ITP ${itp.id} не найден`, err);
          return null;
        });
      }
      return res;
    });

    const mkdResults = await Promise.all(mkdPromises);
    mkdStore.addMany(mkdResults.filter(Boolean) as MKDResponse[]);
  } catch (err) {
    console.error("Ошибка при загрузке ITP:", err);
    itpError.value = "Не удалось загрузить данные. Попробуйте позже.";
  } finally {
    itpLoading.value = false;
  }
};

const handlePageChange = (page: number) => {
  console.log("handlePageChange", page);
  if (page < 1 || page > totalPages.value) return;
  pageable.value = { ...pageable.value, page: page - 1 };
  loadItpList();
};

onMounted(() => {
  loadItpList();
});

// const loading = ref(false);
// const error = ref<string | null>(null);
// const mkdList = ref<MKDResponse[]>([]);
// const pageable = ref<Pageable>({
//   page: 0,
//   size: 10,
//   sort: ["id", "asc"],
// });

// const loadMkdList = async () => {
//   loading.value = true;
//   error.value = null;

//   try {
//     const response: PageMKDResponse = await mkdApi.getAll(pageable.value);
//     mkdList.value = response.content;
//     pageable.value = {
//       ...pageable.value,
//       page: response.number,
//       hasNext: response.number < response.totalPages - 1,
//     };
//   } catch (err) {
//     console.error("Ошибка при загрузке MKD:", err);
//     error.value = "Не удалось загрузить данные. Попробуйте позже.";
//   } finally {
//     loading.value = false;
//   }
// };

// onMounted(() => {
//   loadMkdList();
// });
</script>
