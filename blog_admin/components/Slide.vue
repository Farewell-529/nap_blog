<script setup lang="ts">
import { ArrowLeft, ArrowRight, Home, Newspaper, Tags, BookOpenCheck, MessageSquareMore, LayoutDashboard, Aperture, UserRound, SquareMousePointer, ShieldCheck  } from 'lucide-vue-next'
import { blogInfoStore } from '~/store/blogInfo'
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
        icon: MessageSquareMore,

    },
    {
        name: '黑名单',
        url: '/blocked',
        icon: ShieldCheck,

    },
    {
        name: '浏览',
        url: '/visits',
        icon: SquareMousePointer
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
const blogInfo = computed(() => store.blogInfo)
</script>
<template>
    <div class="mr-12 ">
        <div class="w-56 h-full transition-all duration-300 overflow-hidden" :class="{ closeSlide: !isOpen }">
            <!--  -->
            <div class="w-56 h-full fixed left-0 top-0  bg-[#1d1d1d] text-[#d2d2d2] transition-all duration-300 overflow-hidden"
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
                    <NuxtLink :title="!isOpen ? item.name : ''" :to="item.url"
                        class="flex gap-8 items-center  my-5 font-semibold text-sm  
                     py-2 pl-2 rounded-md  max-h-10 overflow-hidden transition-all duration-300 select-none cursor-pointer" :class="{
                        'opacity-100 w-48': isOpen,
                        'w-11': !isOpen,
                        'hover:bg-gray-200 ': item.url != null,
                        'hover:bg-transparent text-white': item.url == null,
                    }">
                        <span>
                            <component :is="item.icon" />
                        </span>
                        <!-- 防止打开时文字抖动 -->
                        <div v-show="isOpen" class="h-5 overflow-hidden">
                            <span v-if="item.url != null">
                                {{ item.name }}
                            </span>
                            <span v-else>
                                {{ item.name }}
                            </span>
                        </div>
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

.rootNuxtLink {
    background: transparent;
    color: white;
}

/* 当前路径与 nuxt-link 对应的路径完全一致 */
.router-link-exact-active {
    background-color: rgb(224, 224, 224);
    color: black;
}
</style>
