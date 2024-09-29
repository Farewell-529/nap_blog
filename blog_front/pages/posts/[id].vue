<script setup lang="ts">
import 'github-markdown-css/github-markdown.css';
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCalendarCheckOutline, mdiUpdate, mdiPound, mdiEyeOutline, mdiFolderOutline, mdiArrowUpDropCircleOutline } from '@mdi/js';
import { articleByIdApi } from "~/api/article";
import { useMarkdown } from '~/utils/useMarkdown';
const { initializeMarkdown } = useMarkdown();
const route = useRoute()
const articleId = parseInt(route.query.id as string)
const target={
    targetType:'article',
    targetId:articleId
}
const articleInfo = ref<any>({
    title: '',
    createDate: '',
    updateDate: '',
    content: '',
    commentsList:[]
})
const getArticleInfo = async () => {
    const md = await initializeMarkdown();
    const { data } = await articleByIdApi(articleId)
    //属性覆盖
    articleInfo.value = {
        ...data,
        createDate: formatDateWithDay(data.createDate),
        updateDate: data.updateDate != data.createDate ? formatDateWithDay(data.updateDate) + '(已修改)' : null,
    };
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
onMounted(() => {
    getArticleInfo();
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
                <div v-for="(item, index) in articleInfo.tags">
                    <span>
                        {{ item.tagsName }}
                    </span>
                    <span v-if="index < articleInfo.tags.length - 1" class="mx-[2px]">/</span>
                </div>
            </div>
             <div class="flex items-center  ">
                    <svg-icon type="mdi" :path="mdiFolderOutline" class="w-5 mr-1" />
                    <span>
                        分类
                     <NuxtLink :to="{path: '/category/categoryArticle', query: { id: articleInfo.id,categoryName: articleInfo.categoryName}}" class="border-b-[1px] border-b-black pb-1 text-black">
                            {{ articleInfo.categoryName }}
                     </NuxtLink>
                    </span>
                </div>
            <div class="flex items-center ">
                <svg-icon type="mdi" :path="mdiEyeOutline" class="w-3 mr-1" />
                <span>{{ articleInfo.viewCount ? articleInfo.viewCount : 0 }}</span>
            </div>
        </div>
        <Article :content="articleInfo.content">
            <Comments :target :commentsList="articleInfo.commentsList" >
            </Comments>
        </Article>
        <div class="min-h-[60vh]">
        </div>
    </div>
</template>
<style></style>