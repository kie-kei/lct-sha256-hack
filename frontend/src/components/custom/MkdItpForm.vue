<template>
  <form @submit="onSubmit" class="h-full flex flex-col gap-4">
    <div class="flex flex-row gap-4 justify-between items-center">
      <FormField v-slot="{ componentField }" name="number">
        <FormItem>
          <FormLabel>Номер ИТП</FormLabel>
          <FormControl>
            <Input placeholder="Введите номер ИТП" v-bind="componentField" />
          </FormControl>
          <FormDescription />
          <FormMessage />
        </FormItem>
      </FormField>
      <FormField v-slot="{ componentField }" name="address">
        <FormItem>
          <FormLabel>Адрес МКД</FormLabel>
          <FormControl>
            <Input placeholder="Введите адрес МКД" v-bind="componentField" />
          </FormControl>
          <FormDescription />
          <FormMessage />
        </FormItem>
      </FormField>
    </div>
    <div class="flex flex-row gap-4 justify-between items-center">
      <FormField v-slot="{ componentField }" name="unom">
        <FormItem>
          <FormLabel>УНОМ МКД</FormLabel>
          <FormControl>
            <Input placeholder="Введите УНОМ МКД" v-bind="componentField" />
          </FormControl>
          <FormDescription />
          <FormMessage />
        </FormItem>
      </FormField>
      <FormField v-slot="{ componentField }" name="fias">
        <FormItem>
          <FormLabel>ФИАС МКД</FormLabel>
          <FormControl>
            <Input placeholder="Введите ФИАС МКД" v-bind="componentField" />
          </FormControl>
          <FormDescription />
          <FormMessage />
        </FormItem>
      </FormField>
    </div>
    <Button class="cursor-pointer">Отправить</Button>
  </form>
</template>
<script setup lang="ts">
import { toast } from "vue-sonner";
import { useForm } from "vee-validate";
import * as z from "zod";
import { toTypedSchema } from "@vee-validate/zod";
import {
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "../ui/button";
import { itpApi } from "@/api/itp";
const formSchema = toTypedSchema(
  z.object({
    number: z.string({ required_error: "Номер ИТП обязателен" }),
    address: z.string({ required_error: "Адрес МКД обязателен" }),
    unom: z.string({ required_error: "УНОМ МКД обязателен" }),
    fias: z.string({ required_error: "ФИАС МКД обязателен" }),
  }),
);
const form = useForm({
  validationSchema: formSchema,
});
const onSubmit = form.handleSubmit((values) => {
  try {
    itpApi.create({
      number: values.number,
      id: crypto.randomUUID(),
      mkd: {
        address: values.address,
        fias: values.fias,
        unom: values.unom,
      },
    });
    toast.info("ИТП и МКД созданы", {
      description:
        "Создано ИТП с номером: " +
        values.number +
        ". И МКД с адресом: " +
        values.address,
    });
    form.setFieldValue("address", "");
    form.setFieldValue("number", "");
    form.setFieldValue("fias", "");
    form.setFieldValue("unom", "");
  } catch (error) {
    toast.error("Ошибка создания ИТП и МКД");
  }
});
</script>
