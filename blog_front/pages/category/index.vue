<script setup lang="ts">
import { categoryCountsListApi } from "~/api/category";
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiTableLarge } from '@mdi/js';
const countsList = ref([
    {
        id: 0,
        articleCount: 0,
        categoryName: ''
    }
])
const total = ref(0)
const getCountsList = async () => {
    const { data } = await categoryCountsListApi()
    countsList.value = data.recordList
    total.value = data.total
}

onMounted(() => {
    getCountsList()
})
</script>
<template>
    <div class="flex flex-col items-center  mt-15">
        <span class="text-xl font-semibold mb-5">
            目前共有{{ total }}个分类~
        </span>
        <div class="max-w-[920px] flex gap-8">
            <NuxtLink
                :to="{ path: '/category/categoryArticle', query: { id: item.id, categoryName: item.categoryName } }"
                v-for="item in countsList"
                class="flex flex-col gap-2 justify-center items-center font-semibold w-32 h-32 p-1 rounded-2xl mb-5 text-xl bg-white category">
                <svg-icon type="mdi" :path="mdiTableLarge" class="w-8 mr-1" />
                <span>{{ item.categoryName }}</span>
                <span >{{ item.articleCount }}</span>
            </NuxtLink>
        </div>

    </div>
</template>
<style scoped>
.category {
    border: solid 2px rgb(0, 0, 0);
    box-sizing: border-box;
    transition: all 0.3s ease;
    cursor: pointer;
}

.category:hover {
    background-color: rgb(0, 0, 0) !important;
    color: rgb(239, 239, 239) !important;
    transform: scale(1.1);
}

.category:hover .category-count {
    background-color: black !important;
    color: white !important;
}
</style>