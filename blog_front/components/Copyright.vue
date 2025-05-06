<script setup lang="ts">
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCardAccountDetails, mdiSubtitles, mdiCalendarRange, mdiCopyright } from '@mdi/js';
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  author: {
    type: String,
    required: true
  },
  agreementText: {
    type: String,
    required: true
  },
  agreementName: {
    type: String,
    required: true
  },
  agreementUrl: {
    type: String,
    required: true
  },
  createDate: {
    type: String,
    required: true
  }
})
</script>
<template>
  <div class="text-sm font-mono font-bold my-4 p-3 border-[3px]  [border-color:var(--text-color)]">
    <p>文章标题:
      {{ title }}
    </p>
    <p>文章作者:
      {{ author }}
    </p>
    <p class="tracking-tighter">最后修改时间:
      {{ createDate }}
    </p>
    <hr class="my-4 h-[1px] border-0 [background-color:var(--border-color)]">
    <p>本文采用:
      <a :href="agreementUrl" target="_blank" rel="noopener noreferrer" class="router">
        {{ agreementName }}
      </a>
      许可协议
    </p>
    <p>{{ agreementText }}</p>
  </div>
</template>


<style scoped>
.router {
    position: relative;
    padding-bottom: 0.1rem;
    font-size: 16px;
    transition: border-bottom 0.2s ease-in-out, transform 0.2s ease-in-out;
}

.router::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 2px;
    background-color: var(--btn-bg-color);
    /* 初始状态下，宽度为 0，元素不可见 */

    transform: scaleX(0);
    /* 添加过渡效果 */
    transition: transform 0.2s ease-in-out;

}

.router:hover::after {
    /* 悬停时，元素扩展到原始宽度 */
    transform: scaleX(1);
    transform-origin: center;
}
</style>