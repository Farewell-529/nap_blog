<script setup lang="ts">
import '~/assets/css/markdown.css';
import mediumZoom from 'medium-zoom'
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCheckboxMarkedCircleOutline, mdiArrowUpDropCircleOutline } from '@mdi/js';
import { useMarkdown } from '~/utils/useMarkdown';
import { addTocJump, highlightTOC } from "~/utils/commonEvent";
const { initializeMarkdown, handleCopyButtonClick } = useMarkdown();
const scrollPercent = ref(0)
const isToc = ref(false)
const isTop = ref(false)
const toc = ref()
const renderContent = ref('')
const props = defineProps({
    content: String
})
const calculateScrollPercent = () => {
    const markdownBody = document.querySelector('.markdown-body') as HTMLElement;
    if (markdownBody) {
        // 获取页面滚动高度
        const scrollTop = window.scrollY || document.documentElement.scrollTop;
        // 获取页面内容的总高度
        const scrollHeight = markdownBody.scrollHeight;
        // 获取距离顶部位置的高度
        const clientHeight = markdownBody.offsetTop;
        // 计算滚动的百分比，并去掉小数点
        scrollPercent.value = Math.floor((scrollTop / (scrollHeight - clientHeight)) * 100);
        // 当滚动百分比达到或超过100%时，停止进一步计算
        if (scrollPercent.value >= 100) scrollPercent.value = 100;
        if (scrollTop == 0) {
            isTop.value = false
        } else {
            isTop.value = true
        }
    }
};
const initHandle = async (content: string) => {
    if (!content) return;  // 如果 content 为空，直接返回
    const md = await initializeMarkdown();
    renderContent.value = md.render(content);
    toc.value = md.render('\n[toc]\n' + content).match(/<nav class="toc-container">.*<\/nav>/s)?.[0] || '';
    nextTick(() => {
        mediumZoom(document.querySelectorAll('img'));
        addTocJump();
        const tocContainer = document.querySelector('.toc-container') as HTMLElement;
        if (tocContainer && tocContainer.children.length > 0) {
            isToc.value = true;
        }
        window.addEventListener('scroll', highlightTOC);
    });
};

watch(() => props.content, (newContent) => {
    if (newContent) {
        initHandle(newContent);  
    }
});
const topHandle = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth' 
    });
}
onMounted(() => {
    window.addEventListener('scroll', calculateScrollPercent)
    document.addEventListener('click', handleCopyButtonClick);
})
// 在组件卸载时移除事件
onBeforeUnmount(() => {
    document.removeEventListener('click', handleCopyButtonClick);
    window.removeEventListener('scroll', calculateScrollPercent);
});
</script>
<template>
    <div>
        <div class="article-container ">
            <div v-html="renderContent" class="markdown-body "></div>
            <div class="slide-container" v-show="isToc">
                <div v-html="toc"></div>
                <hr class="mt-2 mb-1 h-[0.5px] border-0 bg-black ">
                <div class="flex items-center text-sm gap-1">
                    <svg-icon type="mdi" :path="mdiCheckboxMarkedCircleOutline" class="w-4"></svg-icon>
                    <span> {{ scrollPercent }}%</span>
                </div>
                <button class="text-[14px] flex gap-1 transition-all duration-200 ease-in-out " :class="{
                    'opacity-0 ': !isTop,
                    'opacity-100 ': isTop
                }" @click="topHandle">
                    <svg-icon type="mdi" :path="mdiArrowUpDropCircleOutline" class="w-4" />
                    <span>回到顶部</span>
                </button>
            </div>
        </div>
        <slot>
        </slot>
    </div>
</template>
<style scoped></style>
