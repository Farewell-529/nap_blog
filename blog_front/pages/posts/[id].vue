<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCalendarCheckOutline, mdiUpdate, mdiPound, mdiEyeOutline, mdiFolderOutline } from '@mdi/js';
import { articleByIdApi } from "~/api/article";
const route = useRoute()
const articleId = parseInt(route.query.id as string)
const target = {
    targetType: 'article',
    targetId: articleId
}
const articleInfo = ref<any>({
    title: '',
    createDate: '',
    updateDate: '',
    content: '',
    commentsList: []
})
const createDate=ref<string>('')
const getArticleInfo = async () => {
    const { data } = await articleByIdApi(articleId)
    createDate.value=formatDateWithMinute(data.createDate)
    //属性覆盖
    articleInfo.value = {
        ...data,
        createDate: formatDateWithDay(data.createDate),
        updateDate: data.updateDate != data.createDate ? formatDateWithDay(data.updateDate) + '(已修改)' : null,
    };
    articleInfo.value.commentsList = articleInfo.value.commentsList.sort((a: any, b: any) => new Date(b.createDate).getTime() - new Date(a.createDate).getTime())
}
const formatDateWithMinute = (date: string): string => {
    const d = new Date(date);
    const year = d.getFullYear();
    // 月、日、小时、分钟需补零至两位数，因此目标长度设为2
    const month = String(d.getMonth() + 1).padStart(2, '0'); // 月份从0开始，需+1
    const day = String(d.getDate()).padStart(2, '0');
    const hour = String(d.getHours()).padStart(2, '0');
    const minute = String(d.getMinutes()).padStart(2, '0');

    return `${year} 年 ${month} 月 ${day} 日 ${hour}:${minute}`;
};

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
        <div class="text-3xl font-bold cursor-pointer mb-3 font-mono" style="color: var(--text-color);">
            {{ articleInfo.title}} 
        </div>
        <div class="flex items-center text-[10px] mb-12 h-3 iconDiv gap-3" style="color: var(--minor-text-color);">
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
                    <NuxtLink
                        :to="{ path: '/category/categoryArticle', query: { id: articleInfo.id, categoryName: articleInfo.categoryName } }"
                        class="border-b-[1px]  pb-1" style="border-color:var(--border-color);color: var(--text-color);">
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
            <!-- 具名插槽 -->
            <template #comments>
                <Comments :target :commentsList="articleInfo.commentsList"/>
            </template>
            <template #copyright>
                <Copyright :title="articleInfo.title" :author="'Nap'" 
                :agreementText="'转载或引用本文时请遵守许可协议，注明出处，不得用于商业用途!'" :agreementName="' CC BY-NC-SA 4.0'" 
                :agreementUrl="'https://creativecommons.org/licenses/by-nc-sa/4.0/'"
                :createDate/>
            </template>
        </Article>
        <div class="h-52"></div>
    </div>
</template>
<style></style>