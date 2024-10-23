<script setup lang="ts">

// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCardAccountDetails, mdiHome, mdiTagMultiple, mdiTableLarge, mdiArchive, mdiAccountMultiple } from '@mdi/js';
import { getBloggerInfoApi } from "~/api/blog";
import { type BloggerInfo } from "~/types/BlogInfo";
import { blogInfoStore } from "~/store/blogInfo";
const store = blogInfoStore()
const userInfo = ref<BloggerInfo>({
    bloggerName: "",
    motto: '',
    bio: '',
    avatar: '',
    github_url: '',
    x_url: '',
    bilibili_url: ''
})
const list = [
    {
        name: '首页',
        url: '/',
        icon: mdiHome
    },
    {
        name: '自述',
        url: '/about',
        icon: mdiCardAccountDetails
    },
    {
        name: '标签',
        url: '/tags',
        icon: mdiTagMultiple
    },
    {
        name: '分类',
        url: '/category',
        icon: mdiTableLarge
    },
    {
        name: '友链',
        url: '/friend',
        icon: mdiAccountMultiple
    },
    {
        name: '归档',
        url: '/archive',
        icon: mdiArchive
    }
]
const getUserInfo = async () => {
    const { data } = await getBloggerInfoApi()
    store.setBlogInfo(data)
    userInfo.value = { ...store.blogInfo! }
}
onMounted(() => {
    getUserInfo()
})
</script>
<template>
    <div class="flex  flex-col items-center my-10 relative">
        <div class="w-36 h-8 bg-black mb-3 text-white flex justify-center items-center">
            {{ userInfo.bloggerName }}
        </div>
        <div class="ZCOOL-KuaiLe text-gray-400 text-xs ">
            {{ userInfo.motto }}
        </div>
    </div>
    <div class="flex justify-center gap-8 ">
        <NuxtLink :to="item.url" class="cursor-pointer text-xs router ZCOOL-KuaiLe" v-for="item in list">
            <svg-icon type="mdi" :path="item.icon" class="mb-2" />
            <span>{{ item.name }}</span>
        </NuxtLink>
    </div>
</template>
<style scoped>
@font-face {
    font-family: ZCOOL KuaiLe;
    src: url('../assets/font/ZCOOLKuaiLe-Regular.ttf') format('truetype');
}

.ZCOOL-KuaiLe {
    font-family: "ZCOOL KuaiLe", sans-serif;
    font-weight: 400;
    font-style: normal;
    font-size: medium;
}

.router {
    position: relative;
    padding-bottom: 0.5rem;
    transition: border-bottom 0.2s ease-in-out, transform 0.2s ease-in-out;
}

.router::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 2px;
    background-color: black;
    /* 初始状态下，宽度为 0，元素不可见 */
    transform: scaleX(0);
    /* 缩放从右下角开始 */
    transform-origin: bottom right;
    /* 添加过渡效果 */
    transition: transform 0.2s ease-in-out;

}

.router:hover::after {
    /* 悬停时，元素扩展到原始宽度 */
    transform: scaleX(1);
    /* 扩展的起点在左下角 */
    transform-origin: bottom left;
}

.router-link-exact-active::after {
    height: 2px;
    background-color: black;
    transform: scaleX(1);
}
</style>