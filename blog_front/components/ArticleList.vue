<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCalendarCheckOutline, mdiFolderOutline, mdiChevronDoubleRight, mdiUpdate, mdiPound, mdiEyeOutline } from '@mdi/js';
import { articleListApi } from '~/api/article'
import { type ArticleRes, type ArticleQuery } from '~/types/Article'
import "~/assets/css/main.css"
const showArticleList = ref()
const currentPage = ref(1)
const length = ref(0)
const size = ref(5)
const route = useRoute()
const tagsId = Number(route.query.tagsId!)
const categoryId = Number(route.query.categoryId!)
const query = ref<ArticleQuery>({
    current: 1,
    size: 5,
})

const getArticleList = async () => {
    if (!Number.isNaN(tagsId)) query.value.tagsId = tagsId
    if (!Number.isNaN(categoryId)) query.value.categoryId = categoryId
    const { data } = await articleListApi(query.value)
    length.value = data.total < size.value ? 1 : Math.ceil(data.total / size.value)
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
            viewCountIcon: mdiEyeOutline,
            detailUrl: `/posts/` + item.id
        }
    }).sort((a: any, b: any) => new Date(b.createDate).getTime() - new Date(a.createDate).getTime());
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
        <div v-for="item in showArticleList" class=" flex flex-col items-center mt-5 mb-8">
            <div class="text-3xl font-bold cursor-pointer mb-3 font-mono">
                {{ item.title }}
            </div>

            <div class="flex text-xs  mb-12 h-3" style="color: var(--minor-text-color);">
                <div class="flex items-center pr-2 mr-2 border-r-2 " style="border-color:var(--border-color)">
                    <svg-icon type="mdi" :path="item.calenderIcon" class="w-5 mr-1" />
                    <span class="">
                        发布于{{ item.createDate }}
                    </span>
                </div>
                <div class="flex items-center pr-2 mr-2 border-r-2 " style="border-color:var(--border-color)"
                    v-if="item.updateDate">
                    <svg-icon type="mdi" :path="item.updateIcon" class="w-5 mr-1" />
                    <span>
                        最近更新
                        {{ item.updateDate }}
                    </span>
                </div>
                <div class="flex items-center" style="border-color:var(--border-color)">
                    <svg-icon type="mdi" :path="mdiPound" class="w-3 mr-1" />
                    <div v-for="(tag, index) in item.tagsList">
                        <span>
                            {{ tag.tagsName }}
                        </span>
                        <span v-if="index < item.tagsList.length - 1" class="mx-[2px]">/</span>
                    </div>
                </div>
                <div class="flex items-center pl-2 ml-2 border-l-2 " style="border-color:var(--border-color)">
                    <svg-icon type="mdi" :path="item.fileIcon" class="w-5 mr-1" />
                    <span>
                        分类
                        <NuxtLink
                            style="color: var(--category-text-color); border-color: var(--category-under-border-color);"
                            v-if="item.categoryName"
                            :to="{ path: '/category/categoryArticle', query: { id: item.id, categoryName: item.categoryName } }"
                            class="border-b-[1px]  pb-1 ">
                            {{ item.categoryName }}
                        </NuxtLink>
                        <span v-else style="color: var(--category-text-color);">
                            无
                        </span>
                    </span>
                </div>
                <div class="flex items-center pl-2 ml-2 border-l-2 " style="border-color:var(--border-color)">
                    <svg-icon type="mdi" :path="item.viewCountIcon" class="w-4 mr-1" />
                    <span>
                        {{ item.viewCount }}
                    </span>
                </div>
            </div>

            <div class="w-[800px]  line-clamp-5  mb-8 font-mono">
                {{ item.content }}
            </div>
            <NuxtLink :to="{ path: item.detailUrl, query: { id: item.id } }" class="readBtn" style="">
                阅读全文
                <svg-icon type="mdi" :path="mdiChevronDoubleRight" class="w-5 " />
            </NuxtLink>
        </div>
        <Pagination :currentPage :length :pageVisible="5" @update:modelValue="updateModelValue" />
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
    background-color: var(--btn-bg-color);
    color: var(--btn-text-color);
    width: 7rem;
    height: 2.5rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    border: solid 2px var(--btn-border-color);
    transition: all 0.3s;
}

.readBtn:hover {
    background-color: var(--bg-color);
    color: var(--text-color);
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