<script setup lang="ts">
import '~/assets/css/markdown.css';
import 'github-markdown-css/github-markdown.css';
import { blogInfoStore } from "~/store/blogInfo";
import { type BloggerInfo } from "~/types/BlogInfo";
import "~/assets/css/main.css"
// 获取配置
const config = useRuntimeConfig()
const store = blogInfoStore()
const blogInfo = ref<BloggerInfo>({
    bloggerName: "",
    motto: '',
    bio: '',
    avatar: '',
    github_url: '',
    x_url: '',
    bilibili_url: ''
})
const getUserInfo = async () => {
    if (store.blogInfo) {
        blogInfo.value = { ...store.blogInfo }
    }

}
onMounted(() => {
    getUserInfo()
})
</script>
<template>
    <div class="mt-20 mx-auto">
        <div class="flex flex-col items-center mb-4">
            <img class="w-[100px] mb-2 rounded-md "
                :src="config.public.baseURL + '/static/avatar/' + blogInfo.avatar" alt="">
            <span class="text-xs  font-bold" style="color: var(--minor-text-color)">{{ blogInfo.motto }}</span>
        </div> 
        <Article :content="blogInfo.bio">
        </Article>
    </div>
</template>
<style scoped></style>