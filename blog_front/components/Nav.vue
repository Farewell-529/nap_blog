<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiCardAccountDetails, mdiHome, mdiTagMultiple, mdiTableLarge, mdiArchive, mdiAccountMultiple } from '@mdi/js';
import { getBloggerInfoApi } from "~/api/blog";
import { type BloggerInfo } from "~/types/BlogInfo";
import { blogInfoStore } from "~/store/blogInfo";
import { themeStore } from "~/store/theme";
const theme = themeStore()
const clickBtn = () => {
    theme.toggleTheme()
}
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
  <div class="text-xs">
    <div class="flex  flex-col items-center my-10 relative">
        <div class="w-36 h-8  flex justify-center items-center cursor-pointer mb-3 text-xl bloggerName" 
         @click="clickBtn">
            {{ userInfo.bloggerName }}
        </div>
        <div class="my-2">
            {{ userInfo.motto }}
        </div>
    </div>
    <div class="flex justify-center gap-8 ">
        <NuxtLink :to="item.url" class="cursor-pointer  router " v-for="item in list">
            <svg-icon type="mdi" :path="item.icon" class="mb-2" />
            <span>{{ item.name }}</span>
        </NuxtLink>
    </div>
  </div>
</template>
<style scoped>
.router {
    position: relative;
    padding-bottom: 0.5rem;
    transition: border-bottom 0.2s ease-in-out, transform 0.2s ease-in-out;
}
.bloggerName{
    background-color: var(--btn-bg-color);
    color: var(--btn-text-color);
    border: solid 2px var(--btn-border-color);
    transition: all 0.3s;
}
.bloggerName:hover {
    background-color: var(--bg-color);
    color: var(--text-color);
}

.router::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 2px;
    background-color: var(--btn-bg-color);
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
    background-color: var(--btn-bg-color);
    transform: scaleX(1);
}
</style>