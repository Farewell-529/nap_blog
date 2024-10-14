<script setup lang="ts">
import { Toaster} from 'vue-sonner'
</script>
<template>
  <div>
    <v-app>
      <Nav></Nav>
      <Toaster position="top-right" />
      <NuxtPage />
    </v-app>
  </div>
</template>

<style>
.page-enter-active,
.page-leave-active {
  transition: all 0.2s ease-in-out;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
  filter: blur(1rem);
}
</style>