<script setup lang="ts">
import type { SidebarProps } from "@/components/ui/sidebar";

import { Building2, Map } from "lucide-vue-next";
import NavMain from "@/components/NavMain.vue";
// import NavUser from "@/components/NavUser.vue";

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarRail,
} from "@/components/ui/sidebar";

const props = withDefaults(defineProps<SidebarProps>(), {
  collapsible: "icon",
});

const data = {
  user: {
    name: "shadcn",
    email: "m@example.com",
    avatar: "/avatars/shadcn.jpg",
  },

  navMain: [
    {
      title: "Карта",
      url: "/map",
      icon: Map,
      isActive: true,
    },
    {
      title: "ИТП и МКД",
      url: "/itp",
      icon: Building2,
    },
    {
      title: "Аварии",
      url: "/accidents",
      icon: Building2,
    },
    {
      title: "Статистика",
      url: "/statistics",
      icon: Building2,
    },
  ],
};
</script>

<template>
  <Sidebar v-bind="props">
    <SidebarContent>
      <NavMain :items="data.navMain" />
      <!-- <NavProjects :projects="data.projects" /> -->
    </SidebarContent>
    <SidebarFooter>
      <!-- <NavUser :user="data.user" /> -->
    </SidebarFooter>
    <SidebarRail />
  </Sidebar>
</template>
