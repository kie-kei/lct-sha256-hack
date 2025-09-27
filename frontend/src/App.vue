<template>
  <div id="app">
    <Transition name="fade" mode="out-in">
      <router-view v-slot="{ Component, route }">
        <component :is="Component" :key="route.path" />
      </router-view>
    </Transition>
  </div>
</template>

<script lang="ts" setup>
import { useAuthStore } from "./store/authStore";
</script>
<style scoped></style>
