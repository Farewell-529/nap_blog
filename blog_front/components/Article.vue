<script setup lang="ts">
// import 'github-markdown-css/github-markdown.css';
import '~/assets/css/markdown.css';
import "~/assets/css/main.css"
import mediumZoom from 'medium-zoom'
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCheckboxMarkedCircleOutline, mdiArrowUpDropCircleOutline } from '@mdi/js';
import { useMarkdown } from '~/utils/useMarkdown';
import { themeStore } from "~/store/theme";
const theme = themeStore()
const { initializeMarkdown, handleCopyButtonClick } = useMarkdown();
const scrollPercent = ref(0)
const isToc = ref(false)
const isTop = ref(false)
const toc = ref()
const renderContent = ref('')
const props = defineProps({
    content: String
})
// 为 TOC 中的所有链接添加点击事件监听器
 const addTocJump = () => {
    document.querySelectorAll('.toc-container a').forEach(anchor => {
        anchor.addEventListener('click', (event) => {
            event.preventDefault();
            const targetId = anchor.getAttribute('href')?.slice(1); // 获取目标 ID
            const targetElement = document.getElementById(targetId!); // 查找目标元素
            if (targetElement) {
                const offset = 100; // 设置你想要的距离顶部的偏移量，单位为像素
                const elementPosition = targetElement.getBoundingClientRect().top; // 获取目标元素相对于视口顶部的位置
                const offsetPosition = elementPosition - offset; // 计算偏移后的位置
                window.scrollBy({
                    top: offsetPosition,
                    behavior: 'smooth' // 平滑滚动
                });
            }
        });
    });
}

 const highlightTOC = () => {
    const tocLinks = document.querySelectorAll('.toc-container a');
    const scrollPosition = window.scrollY + 150; // 滚动位置的偏移量，用于调整高亮位置
    let currentActiveLink: Element | null = null;
    tocLinks.forEach(link => {
        const targetId = link.getAttribute('href')?.slice(1); // 获取目标 ID
        const targetElement = document.getElementById(targetId!);
        if (targetElement) {
            //offsetTop距离顶部位置的高度
            const targetPosition = targetElement.offsetTop;
            if (scrollPosition >= targetPosition) {
                currentActiveLink = link; // 记录当前需要高亮的链接
            }
        }
    });
    // 移除所有链接的高亮状态
    tocLinks.forEach(link => {
        link.classList.remove('active');
    });

    // 仅高亮当前滚动到的标题对应的链接
    if (currentActiveLink) {
        (currentActiveLink as HTMLElement).classList.add('active');
    }
}

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

const loadingMarkdownCss = async (isDark: boolean) => {
    // 查找现有的 markdown 样式链接并删除
    const existingLink = document.querySelector('link[href*="github-markdown-css"]');
    if (existingLink) {
        existingLink.remove();
    }

    // 创建新的 <link> 标签，并加载正确的 CSS 文件
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = isDark 
        ? '/_nuxt/node_modules/.pnpm/github-markdown-css@5.6.1/node_modules/github-markdown-css/github-markdown-dark.css'
        : '/_nuxt/node_modules/.pnpm/github-markdown-css@5.6.1/node_modules/github-markdown-css/github-markdown.css';
    link.setAttribute('data-markdown-style', 'true');
    document.head.appendChild(link);
};
watch(theme,()=>{
    loadingMarkdownCss(theme.isDark)
})
onMounted(() => {
    window.addEventListener('scroll', calculateScrollPercent)
    document.addEventListener('click', handleCopyButtonClick);
    loadingMarkdownCss(theme.isDark)
})
// 在组件卸载时移除事件
onBeforeUnmount(() => {
    document.removeEventListener('click', handleCopyButtonClick);
    window.removeEventListener('scroll', calculateScrollPercent);
});
</script>
<template>
    <div>
        <div class="article-container">
            <div v-html="renderContent" class="markdown-body"></div>
            <div class="slide-container font-mono" v-show="isToc">
                <div v-html="toc"></div>
                <hr class="mt-2 mb-1 h-[0.5px] border-0 " style="background-color: var(--text-color);">
                <div class="flex items-center text-sm gap-1">
                    <svg-icon type="mdi" :path="mdiCheckboxMarkedCircleOutline" class="w-4"></svg-icon>
                    <span> {{ scrollPercent }}%</span>
                </div>
                <button class="text-[14px] flex gap-1 transition-all duration-500 ease-in-out " :class="{
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
<style scoped>

</style>
