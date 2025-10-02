<template>
  <div>
    <div class="flex flex-row items-center justify-between gap-4">
      <Input placeholder="Номер ИТП" v-model="filterNumber" />
      <Input placeholder="Адрес МКД" v-model="filterAddress" />
      <Dialog>
        <DialogTrigger>
          <Button @click="handleAddItp" class="cursor-pointer">Добавить</Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Создание ИТП и МКД</DialogTitle>
          </DialogHeader>
          <MkdItpForm />
        </DialogContent>
      </Dialog>
    </div>
    <ItpTable
      :itp="data"
      :pageable="pageable"
      :total-items="totalItems"
      :total-pages="totalPages"
      @page-changed="handlePageChange"
    />
  </div>
</template>
<script setup lang="ts">
import { itpApi } from "@/api/itp";
import { mkdApi } from "@/api/mkd";
import MkdItpForm from "@/components/custom/MkdItpForm.vue";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  type ITPResponse,
  type MKDResponse,
  type Pageable,
  type PageITPResponse,
} from "@/api/types";
import { onMounted } from "vue";

import { ref } from "vue";
import ItpTable from "@/components/custom/ItpTable.vue";
import { computed } from "vue";
import Input from "@/components/ui/input/Input.vue";
import { Button } from "@/components/ui/button";
import { useDebounceFn } from "@vueuse/core";
import { watch } from "vue";

const pageable = ref<Pageable>({
  page: 0,
  size: 20,
  sort: ["id", "asc"],
});
const allMkd = ref<MKDResponse[]>([]);
const allItp = ref<ITPResponse[]>([]);
const filterNumber = ref<string>("");
const filterAddress = ref<string>("");

const fetchNumberSearchResults = async () => {
  const response = await itpApi.search(filterNumber.value, pageable.value);
  allItp.value = response.content;
};

const debouncedFetch = useDebounceFn(fetchNumberSearchResults, 300, {
  maxWait: 1000,
  // rejectOnCancel: true // если нужно обрабатывать отмену
});

watch([filterNumber], () => {
  debouncedFetch();
});

const fetchAddressSearchResults = async () => {
  const response = await mkdApi.search(filterAddress.value, pageable.value);
  const itpPromises = response.content.map((mkd) => {
    return itpApi.getById(mkd.itpId).catch((err) => {
      console.warn(`ITP для MKD ${mkd.itpId} не найден`, err);
      return null;
    });
  });

  const itps = (await Promise.all(itpPromises)).filter((x) => x !== null);
  allItp.value = itps;
};

const debouncedAddressFetch = useDebounceFn(fetchAddressSearchResults, 300, {
  maxWait: 1000,
  // rejectOnCancel: true // если нужно обрабатывать отмену
});

watch([filterAddress], () => {
  debouncedAddressFetch();
});

const data = computed(() => {
  const d = allItp.value;
  const mapped = d.map((x) => {
    const mkd = allMkd.value.find((y) => y.itpId === x.id);
    return {
      mkdId: mkd?.id || "",
      id: x.id,
      number: x.number,
      address: mkd?.address || "",
      unom: mkd?.unom || "",
    };
  });
  return mapped;
});
const itpLoading = ref(false);
const itpError = ref<string | null>(null);
const totalItems = ref(0);
const totalPages = ref(0);
const loadItpList = async () => {
  itpLoading.value = true;
  itpError.value = null;

  try {
    const response: PageITPResponse = await itpApi.getAll(pageable.value);
    allItp.value = response.content;
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

const handlePageChange = (page: number) => {
  console.log("handlePageChange", page);
  if (page < 1 || page > totalPages.value) return;
  pageable.value = { ...pageable.value, page: page - 1 };
  loadItpList();
};
const handleAddItp = () => {};
onMounted(() => {
  loadItpList();
});
</script>
