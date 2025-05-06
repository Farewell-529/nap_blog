<script setup lang="ts">
import '~/assets/css/markdown.css';
import 'github-markdown-css/github-markdown.css';
import { blogInfoStore } from "~/store/blogInfo";
import { type BloggerInfo } from "~/types/BlogInfo";
import "~/assets/css/main.css"
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiAlphaXBox, mdiGithub, mdiEmailOutline } from '@mdi/js';
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
    email: ''
})
const iconArr = computed(() => [
    {
        name: 'X',
        icon: mdiAlphaXBox,
        url: blogInfo.value.x_url,
    },
    {
        name: 'Github',
        icon: mdiGithub,
        url: blogInfo.value.github_url,
    },
    {
        name: 'Email',
        icon: mdiEmailOutline,
        url: 'mailto:farewell@0u0.ren'
    },
])

const getUserInfo = () => {
    if (store.blogInfo) {
        blogInfo.value = { ...store.blogInfo }
    }

}
onMounted(() => {
    getUserInfo()
})
</script>
<template>
    <div class="mt-20 mx-auto ">
        <div class="flex flex-col items-center  mb-4">
            <img class="w-[100px] mb-2 rounded-md " :src="config.public.baseURL + '/static/avatar/' + blogInfo.avatar"
                alt="">
            <div class="flex gap-3">
                <a v-for="item in iconArr" :key="item.name" :title="item.name" class="cursor-pointer" :href="item.url"
                    target="_blank" rel="noopener noreferrer">
                    <svg-icon type="mdi" :path="item.icon" />
                </a>
            </div>
        </div>
        <Article :content="blogInfo.bio">
        </Article>
    </div>
</template>
<style scoped></style>