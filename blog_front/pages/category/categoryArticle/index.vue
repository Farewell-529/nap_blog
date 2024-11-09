<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiTableLarge } from '@mdi/js';
const route = useRoute()
const articleCount = ref(route.query.articleCount);
const categorysName = ref(route.query.categoryName);
const callback = () => {
    useRouter().back()
}
</script>
<template>
    <div class="max-w-[800px] relative">
        <div class="flex items-center w-fit  p-1 rounded-md cursor-pointer text-sm font-semibold absolute left-3 category"
            @click="callback">
            <svg-icon type="mdi" :path="mdiTableLarge" class="w-4 mr-1" />
            <span>
                {{ categorysName }}
            </span>
            <span class="ml-2 p-1 ">
                {{ articleCount }}
            </span>
        </div>
        <ArticleList></ArticleList>
        <!-- <div class="min-w-[800px]">
            <div v-if="articleCount > 0" v-for="item in showArticleList" class=" flex flex-col items-center mt-20 ">
                <div class="text-3xl font-normal cursor-pointer mb-3">
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
                        <div v-for="(category, index) in item.categorysList">
                            <span>
                                {{ category.categorysName }}
                            </span>
                            <span v-if="index < item.categorysList.length - 1" class="mx-[2px]">/</span>
                        </div>
                    </div>
                    <div class="flex items-center pl-2 ml-2 border-l-2 border-gray-500">
                        <svg-icon type="mdi" :path="item.fileIcon" class="w-5 mr-1" />
                        <span>
                            分类
                            <a href="/" class="border-b-[1px] border-b-black pb-1 text-black">
                                {{ item.categoryName }}
                            </a>
                        </span>
                    </div>
                </div>

                <div class="w-[800px]  line-clamp-5  mb-8">
                    {{ item.content }}
                </div>
                <NuxtLink :to="{ path: item.detailUrl, query: { id: item.id } }" class="readBtn">
                    阅读全文
                    <svg-icon type="mdi" :path="mdiChevronDoubleRight" class="w-5 " />
                </NuxtLink>
            </div>
            <div v-else class="text-xl mt-5 text-center ">
                暂无数据....
            </div>
        </div>

        <v-pagination class="mt-10" v-model="page" :length="pagelength" :total-visible="pageVisible"
            @update:modelValue="updateModelValue" density="compact" /> -->

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


.category {
    border: solid 2px var(--btn-border-color);
    background: var(--bg-color);
    box-sizing: border-box;
    transition: all 0.2s ease;
}

.category:hover {
    background-color: var( --btn-bg-color) !important;
    color: var(--btn-text-color) !important;
    transform: scale(1.05);
}
</style>