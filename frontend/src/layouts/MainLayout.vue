<template>
  <div class="h-screen">
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header
          class="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12"
        >
          <div class="flex items-center gap-2 px-4">
            <SidebarTrigger class="-ml-1" />
            <Separator orientation="vertical" class="mr-2 h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem class="hidden md:block">
                  <BreadcrumbLink @click="handleBackPath" href="#">
                    {{ route.meta.breadcrumb }}
                  </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator class="hidden md:block" />
                <BreadcrumbItem>
                  <BreadcrumbPage v-if="applicationStore.secondLayer">{{
                    applicationStore.secondLayer
                  }}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>
        <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
          <main
            class="md:rounded-tl-[1.25rem] pb-18 md:pb-0 md:mt-6 w-full bg-background h-full"
          >
            <router-view />
          </main>
        </div>
      </SidebarInset>
    </SidebarProvider>
  </div>
</template>
<script setup lang="ts">
import AppSidebar from "@/components/AppSidebar.vue";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Separator } from "@/components/ui/separator";
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { useApplicationStore } from "@/store/applicationStore";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const applicationStore = useApplicationStore();
const handleBackPath = () => {
  router.push(route.meta.backPath!);
  applicationStore.secondLayer = "";
};
</script>
