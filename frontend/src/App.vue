<template>
  <Toaster />
  <div id="app">
    <Transition name="fade" mode="out-in">
      <router-view v-slot="{ Component, route }">
        <component :is="Component" :key="route.path" />
      </router-view>
    </Transition>
  </div>
</template>

<script lang="ts" setup>
// import { useAuthStore } from "./store/authStore";
import { Toaster } from "@/components/ui/sonner";
import "vue-sonner/style.css";
</script>
<style scoped></style>
