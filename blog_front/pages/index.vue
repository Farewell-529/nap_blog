<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCalendarCheckOutline, mdiFolderOutline, mdiChevronDoubleRight, mdiUpdate, mdiPound,mdiEyeOutline } from '@mdi/js';
import { articleListApi } from '~/api/article'
import { type ArticleRes, type ArticleQuery } from '~/types/Article'
const showArticleList = ref()
const page = ref(1)
const pagelength = ref()
const pageVisible = ref(5)
const query = ref<ArticleQuery>({
    current: 1,
    size: 5,
})
const getArticleList = async () => {
    const { data } = await articleListApi(query.value)
    pagelength.value = data.total < pageVisible.value ? 1 : Math.ceil(data.total / pageVisible.value)
    showArticleList.value = data.recordList.map((item: ArticleRes) => {
        let updateDate = null
        if (item.updateDate != item.createDate) {
            updateDate = item.updateDate
        }
        return {
            ...item,
            createDate: new Date(item.createDate!).toLocaleDateString('zh-CN'),
            updateDate: item.updateDate !== item.createDate
                ? new Date(item.updateDate!).toLocaleDateString('zh-CN')
                : null,
            calenderIcon: mdiCalendarCheckOutline,
            fileIcon: mdiFolderOutline,
            updateIcon: mdiUpdate,
            viewCountIcon:mdiEyeOutline,
            detailUrl: `posts/` + item.id
        }
    }).sort((a:any, b:any) => new Date(b.createDate).getTime() - new Date(a.createDate).getTime());
}
const updateModelValue = (currentPage: number) => {
    query.value.current = currentPage
    getArticleList()
}
onMounted(() => {
    getArticleList()
})
</script>
<template>
    <div class="pb-10">
        <div v-for="item in showArticleList" class=" flex flex-col items-center mt-20">
            <div class="text-3xl font-bold cursor-pointer mb-3">
                {{ item.title }}
            </div>

            <div class="flex text-xs text-gray-500 mb-12 h-3">
                <div class="flex items-center pr-2 mr-2 border-r-2 border-gray-500">
                    <svg-icon type="mdi" :path="item.calenderIcon" class="w-5 mr-1" />
                    <span class="">
                        发表于{{ item.createDate }}
                    </span>
                </div>
                <div class="flex items-center pr-2 mr-2 border-r-2 border-gray-500" v-if="item.updateDate">
                    <svg-icon type="mdi" :path="item.updateIcon" class="w-5 mr-1" />
                    <span>
                        最近更新
                        {{ item.updateDate }}
                    </span>
                </div>
                <div class="flex items-center ">
                    <svg-icon type="mdi" :path="mdiPound" class="w-3 mr-1" />
                    <div v-for="(tag, index) in item.tagsList">
                        <span>
                            {{ tag.tagsName }}
                        </span>
                        <span v-if="index < item.tagsList.length - 1" class="mx-[2px]">/</span>
                    </div>
                </div>
                <div class="flex items-center pl-2 ml-2 border-l-2 border-gray-500">
                    <svg-icon type="mdi" :path="item.fileIcon" class="w-5 mr-1" />
                    <span>
                        分类
                     <NuxtLink v-if="item.categoryName":to="{path: '/category/categoryArticle', query: { id: item.id,categoryName: item.categoryName}}" class="border-b-[1px] border-b-black pb-1 text-black">
                            {{ item.categoryName }}
                     </NuxtLink>
                     <span v-else class="text-black">
                            无
                     </span>
                    </span>
                </div>
                <div class="flex items-center pl-2 ml-2 border-l-2 border-gray-500" >
                    <svg-icon type="mdi" :path="item.viewCountIcon" class="w-4 mr-1" />
                    <span>
                        {{ item.viewCount }}
                    </span>
                </div>
            </div>

            <div class="w-[800px]  line-clamp-5  mb-8">
                {{ item.content }}
            </div>
            <NuxtLink :to="{ path: item.detailUrl, query: { id: item.id} }" class="readBtn">
                阅读全文
                <svg-icon type="mdi" :path="mdiChevronDoubleRight" class="w-5 " />
            </NuxtLink>
        </div>
        <v-pagination class="mt-10" v-model="page" :length="pagelength" :total-visible="pageVisible"
            @update:modelValue="updateModelValue" density="compact" />
    </div>
</template>
<style scoped>
html {
    scroll-behavior: smooth;
}

.line-clamp-5 {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.text-ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.readBtn {
    background-color: black;
    color: white;
    width: 7rem;
    height: 2.5rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    border: solid 2px black;
    transition: all 0.3s;
    /* 添加过渡效果 */
}

.readBtn:hover {
    background-color: white;
    color: black;
}

.iconDiv {
    display: flex;
    align-items: center;
    padding-right: 0.5rem;
    margin-right: 0.5rem;
    border-right-width: 2px;
    border-color: rgb(117, 117, 117);
}
</style>