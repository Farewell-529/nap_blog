<script setup lang="ts">
import { ArrowLeft, ArrowRight, Home, Newspaper, Tags, BookOpenCheck, MessageSquareMore, LayoutDashboard, Aperture, UserRound } from 'lucide-vue-next'
import { blogInfoStore } from '~/store/blogInfo'
import { getBloggerInfoApi } from '~/api/blog'
import { type BlogInfo } from "~/types/BlogInfo";

const list = [
    {
        name: '仪表盘',
        url: '/',
        icon: Home
    },
    {
        name: '文章',
        url: '/articles',
        icon: Newspaper
    },
    {
        name: '草稿',
        url: '/draft',
        icon: BookOpenCheck
    },

    {
        name: '分类',
        url: '/category',
        icon: LayoutDashboard
    },
    {
        name: '标签',
        url: '/tags',
        icon: Tags
    },
    {
        name: '评论',
        url: '/comments',
        icon: MessageSquareMore
    },
    {
        name: '朋友圈',
        url: '/friends',
        icon: Aperture
    },
    {
        name: '个人信息',
        url: '/blogger',
        icon: UserRound
    }
]
const isOpen = ref(true)
const toggleOpen = () => {
    isOpen.value = !isOpen.value
}
const store = blogInfoStore()
const blogInfo = ref<any>({})
const getUserInfo = async () => {
    const {data,code} = await getBloggerInfoApi()
    if (code!=200) {
        useRouter().push('/login/')
        return
    }
    blogInfo.value=data
    store.setBlogInfo(blogInfo.value)
};
onMounted(() => {
    getUserInfo()
})
</script>
<template>
    <div class="mr-12 ">
        <div class="w-56 h-full  transition-all duration-300 overflow-hidden" :class="{ closeSlide: !isOpen }">
            <div class="w-56 h-full fixed  bg-[#1d1d1d] text-[#d2d2d2] transition-all duration-300 overflow-hidden"
                :class="{ closeSlide: !isOpen }">
                <div class="flex justify-between  p-3 ">
                    <div v-if="isOpen" class="text-xl font-semibold cursor-pointer h-10" 
                        @click="useRouter().push('/blogger')">
                        <span v-if="store.blogInfo?.bloggerName">{{ store.blogInfo?.bloggerName }}</span>
                        <span v-else>加载中...</span>
                    </div>
                    <div class="text-xl font-semibold cursor-pointer h-10" @click="toggleOpen">
                        <ArrowLeft v-if="isOpen" />
                        <ArrowRight v-else />
                    </div>
                </div>
                <div v-for="(item, index) in list" :key="index" class="hover:text-black w-48" v-ripple>
                    <NuxtLink  :title="!isOpen ? item.name : ''" :to="item.url" class="flex gap-8 items-center  my-8 font-semibold text-sm hover:bg-gray-200  
                     py-2 pl-2 rounded-md  max-h-10 overflow-hidden transition-all duration-300" 
                        :class="isOpen ? 'opacity-100 w-48 ' : 'w-11'">
                        <span>
                            <component :is="item.icon" />
                        </span>
                        <!-- 防止打开时文字抖动 -->
                        <span v-show="isOpen" class="h-5 overflow-hidden">
                            {{ item.name }}</span>
                    </NuxtLink>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
.closeSlide {
    width: 3rem;
    margin-right: -2rem;
    /* transform: translateX(-15rem); */
}

/* 当前路径与 nuxt-link 对应的路径完全一致 */
.router-link-exact-active {
    background-color: rgb(224, 224, 224);
    color: black;
}
</style>
