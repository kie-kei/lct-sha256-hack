<script setup lang="ts">
import type { LucideIcon } from "lucide-vue-next";
import { Collapsible } from "@/components/ui/collapsible";
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { useRouter } from "vue-router";

defineProps<{
  items: {
    title: string;
    url: string;
    icon?: LucideIcon;
    isActive?: boolean;
    items?: {
      title: string;
      url: string;
    }[];
  }[];
}>();

const router = useRouter();
const handleMenuClick = (url: string) => {
  router.push(url);
};
</script>

<template>
  <SidebarGroup>
    <SidebarGroupLabel>SHA-256</SidebarGroupLabel>
    <SidebarMenu>
      <Collapsible
        v-for="item in items"
        :key="item.title"
        as-child
        :default-open="item.isActive"
        class="group/collapsible"
      >
        <SidebarMenuItem>
          <SidebarMenuButton
            :tooltip="item.title"
            @click="handleMenuClick(item.url)"
            class="brightness-75 cursor-pointer transition-colors duration-100"
          >
            <component :is="item.icon" v-if="item.icon" />
            <span>{{ item.title }}</span>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </Collapsible>
    </SidebarMenu>
  </SidebarGroup>
</template>
