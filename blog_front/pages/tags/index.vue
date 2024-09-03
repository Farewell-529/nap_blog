<script setup lang="ts">
import { tagsCountsListApi } from "~/api/tag";
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiPound } from '@mdi/js';
const countsList = ref([
    {
        id: 0,
        articleCount: 0,
        tagsName: ''
    }
])
const total = ref(0)
const getCountsList = async () => {
    const { data } = await tagsCountsListApi()
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
            目前共有{{ total }}个标签~
        </span>
        <div class="max-w-[920px] flex gap-5 flex-wrap">
            <NuxtLink
                :to="{ path: '/tags/tagsArticle', query: { id: item.id, tagsName: item.tagsName, articleCount: item.articleCount } }"
                v-for="item in countsList"
                class="flex items-center text-sm font-semibold p-1 rounded-md bg-black cursor-pointer  tag">
                <svg-icon type="mdi" :path="mdiPound" class="w-4 mr-1" />
                <span>{{ item.tagsName }}</span>
                <span class="ml-2 p-1 bg-white text-black rounded-sm tag-count">{{ item.articleCount }}</span>
            </NuxtLink>
        </div>

    </div>
</template>
<style scoped>
.tag {
    border: solid 2px rgb(0, 0, 0);
    box-sizing: border-box;
    transition: all 0.3s ease;
}

.tag:hover {
    background-color: white !important;
    color: black !important;
    transform: scale(1.1);
}

.tag:hover .tag-count {
    background-color: black !important;
    color: white !important;
}
</style>