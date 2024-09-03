<script setup lang="ts">
import '~/assets/css/markdown.css';
import 'github-markdown-css/github-markdown.css';
import mediumZoom from 'medium-zoom'
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCheckboxMarkedCircleOutline, mdiArrowUpDropCircleOutline } from '@mdi/js';
import { useMarkdown } from '~/api/useMarkdown';
import { addTocJump, highlightTOC } from "~/utils/commonEvent";
const { initializeMarkdown, handleCopyButtonClick } = useMarkdown();
const isToc = ref(false)
const isTop = ref(false)
import { type User } from "~/types/User";
import { userStore } from "~/store/userInfo";
const store = userStore()
const userInfo = ref<User>({
    motto: '',
    avatar: '',
    bio: ''
})
const scrollPercent = ref(0)
const toc = ref()
const getUserInfo = async () => {
    const md = await initializeMarkdown()
    if (store.user) {
        userInfo.value = { ...store.user }
        const bio = store.user.bio
        toc.value = md.render('\n[toc]\n' + store.user.bio).match(/<nav class="toc-container">.*<\/nav>/s)?.[0] || '';
        userInfo.value.bio = md.render(bio)
    }

    nextTick(() => {
        mediumZoom(document.querySelectorAll('img'));
        addTocJump()
        //判断是否有标题导航
        const tocContainer = document.querySelector('.toc-container') as HTMLElement;
        if (tocContainer.children.length > 0) {
            isToc.value = true
        }
        // 监听滚动事件以实现高亮效果
        window.addEventListener('scroll', highlightTOC);

    })
}
const topHandle = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth' // 平滑滚动
    });
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
onMounted(() => {
    getUserInfo()
    document.removeEventListener('click', handleCopyButtonClick);
    window.addEventListener('scroll', calculateScrollPercent)
})
// 在组件卸载时移除事件
onBeforeUnmount(() => {
    document.removeEventListener('click', handleCopyButtonClick);
    window.removeEventListener('scroll', calculateScrollPercent);
});
</script>
<template>
    <div class="mt-20 mx-auto">
        <div class="flex flex-col items-center mb-4">
            <img class="w-[100px] mb-2 rounded-md "
                :src="useRuntimeConfig().public.baseURL + '/static/user/avatar/' + userInfo.avatar" alt="">
            <span class="text-xs text-gray-500 font-bold">{{ userInfo.motto }}</span>
        </div>
        <div class="article-container">
            <div class="markdown-body" v-html="userInfo.bio"></div>
            <div class="slide-container" v-show="isToc">
                <div v-html="toc"></div>
                <hr class="mt-2 mb-1 h-[0.5px] border-0 bg-black ">
                <div class="flex items-center text-[14px] gap-1">
                    <svg-icon type="mdi" :path="mdiCheckboxMarkedCircleOutline" class="w-4"></svg-icon>
                    <span> {{ scrollPercent }}%</span>
                </div>
                <button  class="text-[14px] flex gap-1 transition-all duration-200 ease-in-out" :class="{
                    'opacity-0 ': !isTop,
                    'opacity-100 ': isTop
                }" @click="topHandle">
                    <svg-icon type="mdi" :path="mdiArrowUpDropCircleOutline" class="w-4" />
                    <span>回到顶部</span>
                </button>
            </div>
        </div>
    </div>
</template>
<style scoped></style>