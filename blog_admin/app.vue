<script setup lang="ts">
//关闭浏览器警告
// import 'default-passive-events'
import { Toaster } from 'vue-sonner'
import { config } from 'md-editor-v3';

config({
  editorConfig: {
    // 输入渲染延迟（ms）
    renderDelay: 0
  }
});
</script>
<template>
  <div>
    <v-app>
     <div class="flex">
      <Slide/>
      <Toaster position="top-center" richColors  />
        <NuxtPage class="px-1"/>
     </div>
    </v-app>
  </div>
</template>
