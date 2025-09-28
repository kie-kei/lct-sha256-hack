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
          <!-- <form @submit="onSubmit" class="h-full">
            <div class="flex flex-col gap-2">
              <FormField v-slot="{ componentField }">
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input placeholder="shadcn" v-bind="componentField" />
                  </FormControl>
                  <FormDescription />
                  <FormMessage />
                </FormItem>
              </FormField>
            </div>
            <div class="flex flex-col gap-2">
              <Label>Номер МКД</Label>
              <Input placeholder="Введите номер МКД" />
            </div>
            <div class="flex flex-col gap-2">
              <Label>Номер МКД</Label>
              <Input placeholder="Введите номер МКД" />
            </div>
          </form> -->
          <DialogFooter>
            <Button class="cursor-pointer">Отправить</Button>
          </DialogFooter>
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
import type { MKDResponse, Pageable, PageITPResponse } from "@/api/types";
import { onMounted } from "vue";

import { ref } from "vue";
import { useItpStore } from "@/store/itpStore";
import { useMkdStore } from "@/store/mkdStore";
import ItpTable from "@/components/custom/ItpTable.vue";
import { computed } from "vue";
import Input from "@/components/ui/input/Input.vue";
import { Button } from "@/components/ui/button";

const pageable = ref<Pageable>({
  page: 0,
  size: 20,
  sort: ["id", "asc"],
});

const filterNumber = ref<string>("");
const filterAddress = ref<string>("");
const itpStore = useItpStore();
const mkdStore = useMkdStore();
const data = computed(() => {
  let d = itpStore.getAll();
  if (filterNumber !== undefined || filterNumber !== "") {
    d = d.filter((x) =>
      x.number.toLowerCase().includes(filterNumber.value.toLowerCase()),
    );
  }
  let mapped = d.map((x) => {
    const mkd = mkdStore.getAll().find((y) => y.itpId === x.id);
    return {
      mkdId: mkd?.id || "",
      id: x.id,
      number: x.number,
      address: mkd?.address || "",
      unom: mkd?.unom || "",
    };
  });
  if (filterAddress !== undefined || filterAddress !== "") {
    mapped = mapped.filter((x) =>
      x.address.toLowerCase().includes(filterAddress.value.toLowerCase()),
    );
  }
  return mapped;
});
const itpLoading = ref(false);
const itpError = ref<string | null>(null);
const totalItems = ref(0);
const totalPages = ref(0);
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
const handleAddItp = () => {};
onMounted(() => {
  loadItpList();
});
</script>
