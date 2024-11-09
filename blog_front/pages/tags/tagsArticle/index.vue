<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import {mdiPound } from '@mdi/js';
const route = useRoute()
const articleCount = Number(route.query.articleCount) || 0;
const tagsName = route.query.tagsName;
const callback = () => {
    useRouter().back()
}
</script>
<template>
    <div class="max-w-[800px] relative">
        <div class="flex items-center w-fit  p-1 rounded-md cursor-pointer text-sm font-semibold absolute left-3 tag"
            @click="callback">
            <svg-icon type="mdi" :path="mdiPound" class="w-4 mr-1" />
            <span>
                {{ tagsName }}
            </span>
            <span class="ml-2 p-1  rounded-sm tag-count " style="background: var(--bg-color); color: var(--text-color);">
                {{ articleCount }}
            </span>
        </div>
        <ArticleList></ArticleList>
    </div>
</template>
<style scoped>

.tag {
    border: solid 2px var(--btn-border-color);
    background-color: var(--btn-bg-color);
    color: var(--btn-text-color);
    box-sizing: border-box;
    transition: all 0.2s ease;
}

.tag:hover {
    background-color: var(--bg-color) !important;
    color: var(--text-color) !important;
    transform: scale(1.05);
}

.tag:hover .tag-count {
    background-color: var(--btn-bg-color) !important;
    color: var(--btn-text-color) !important;
}
</style>