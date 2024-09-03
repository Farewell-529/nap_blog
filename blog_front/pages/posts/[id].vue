<script setup lang="ts">
import 'github-markdown-css/github-markdown.css';
import mediumZoom from 'medium-zoom'
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCalendarCheckOutline, mdiUpdate, mdiPound, mdiEyeOutline, mdiCheckboxMarkedCircleOutline, mdiArrowUpDropCircleOutline } from '@mdi/js';
import { articleByIdApi } from "~/api/article";
import { useMarkdown } from '~/api/useMarkdown';
import { addTocJump, highlightTOC } from "~/utils/commonEvent";
const { initializeMarkdown, handleCopyButtonClick } = useMarkdown();
const route = useRoute()
const articleId = parseInt(route.query.id as string)
const scrollPercent = ref(0)
const isToc = ref(false)
const isTop = ref(false)
const articleInfo = ref<any>({
    title: '',
    createDate: '',
    updateDate: '',
    content: '',
    toc: '' // 
})
const getArticleInfo = async () => {
    const md = await initializeMarkdown();
    const { data } = await articleByIdApi(articleId)
    const renderedContent = md.render(data.content);
    const toc = md.render('\n[toc]\n' + data.content).match(/<nav class="toc-container">.*<\/nav>/s)?.[0] || '';

    //属性覆盖
    articleInfo.value = {
        ...data,
        createDate: formatDateWithDay(data.createDate),
        updateDate: data.updateDate != data.createDate ? formatDateWithDay(data.updateDate) + '(已修改)' : null,
        content: renderedContent,
        toc
    };
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
const formatDateWithDay = (date: string) => {
    const d = new Date(date);
    const dayNames = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    // 获取日期的年、月、日
    const year = d.getFullYear();
    const month = d.getMonth() + 1; // 月份是从0开始的，所以需要+1
    const day = d.getDate();
    // 获取星期几
    const dayOfWeek = dayNames[d.getDay()];
    // 拼接日期和星期几，格式为****年**月*日 星期*
    return `${year}年${month}月${day}日 ${dayOfWeek}`;
};

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
const topHandle = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth' // 平滑滚动
    });
}
// 在组件挂载时绑定事件
onMounted(() => {
    getArticleInfo();
    document.addEventListener('click', handleCopyButtonClick);
    window.addEventListener('scroll', calculateScrollPercent)

});
// 在组件卸载时移除事件
onBeforeUnmount(() => {
    document.removeEventListener('click', handleCopyButtonClick);
    window.removeEventListener('scroll', calculateScrollPercent);
});
</script>
<template>
    <div class="flex flex-col items-center mt-20">

        <div class="text-3xl font-bold cursor-pointer mb-3">
            {{ articleInfo.title }}
        </div>
        <div class="flex items-center text-[10px] text-gray-500 mb-12 h-3 iconDiv gap-3">

            <div v-if="articleInfo.updateDate" class="flex items-center ">
                <svg-icon type="mdi" :path="mdiUpdate" class="w-4 mr-1" />
                <span>
                    {{ articleInfo.updateDate }}
                </span>
            </div>

            <div v-else class="flex items-center">
                <svg-icon type="mdi" :path="mdiCalendarCheckOutline" class="w-4 mr-1" />
                <span>
                    {{ articleInfo.createDate }}
                </span>
            </div>

            <div class="flex items-center ">
                <svg-icon type="mdi" :path="mdiPound" class="w-3 mr-1" />
                <div v-for="(item, index) in articleInfo.selectedTags">
                    <span>
                        {{ item.tagsName }}
                    </span>
                    <span v-if="index < articleInfo.selectedTags.length - 1" class="mx-[2px]">/</span>
                </div>
            </div>
            <div class="flex items-center ">
                <svg-icon type="mdi" :path="mdiEyeOutline" class="w-3 mr-1" />
                <span>{{ articleInfo.viewCount ? articleInfo.viewCount : 0 }}</span>
            </div>
        </div>
        <div class="article-container">
            <div v-html="articleInfo.content" class="markdown-body"></div>
            <div class="slide-container" v-show="isToc">
                <div v-html="articleInfo.toc"></div>
                <hr class="mt-2 mb-1 h-[0.5px] border-0 bg-black ">
                <div class="flex items-center text-[14px] gap-1">
                    <svg-icon type="mdi" :path="mdiCheckboxMarkedCircleOutline" class="w-4"></svg-icon>
                    <span> {{ scrollPercent }}%</span>
                </div>
                <button class="text-[14px] flex gap-1 transition-all duration-200 ease-in-out" :class="{
                'opacity-0 ': !isTop,
                'opacity-100 ': isTop
            }" @click="topHandle">
                    <svg-icon type="mdi" :path="mdiArrowUpDropCircleOutline" class="w-4" />
                    <span>回到顶部</span>
                </button>
            </div>
        </div>
        <div class="min-h-[60vh]">
        </div>
    </div>
</template>
<style></style>