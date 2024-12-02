<script setup lang="ts">
import { ArrowLeft, ArrowRight, Home, Newspaper, Tags, BookOpenCheck, MessageSquareMore, LayoutDashboard, Aperture, UserRound, SquareMousePointer, ShieldCheck } from 'lucide-vue-next'
import { blogInfoStore } from '~/store/blogInfo'
import { getBloggerInfoApi } from "~/api/blog";
import * as echarts from 'echarts';
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
const resizeObserver = ref<ResizeObserver | null>(null);
const isOpen = ref(true)
const resizeCharts = () => {
    const dom = document.getElementById("viewChart");
    if (dom) {
        const chartInstance = echarts.getInstanceByDom(dom);
        if (chartInstance) {
            chartInstance.resize();
        }
    }
};
const toggleOpen = () => {
    isOpen.value = !isOpen.value;
};
const store = blogInfoStore()
const blogInfo = ref()
const getBlogInfo = async () => {
    if (!store.blogInfo) {
        const { data } = await getBloggerInfoApi()
        store.setBlogInfo(data)
        blogInfo.value = data
        return
    }
    blogInfo.value = store.blogInfo
}
onMounted(() => {
    getBlogInfo()
    const sidebar = document.getElementById("slide");
    if (sidebar) {
        resizeObserver.value = new ResizeObserver(() => {
            resizeCharts(); // 侧边栏宽度变化时实时调整图表
        });
        //observe 方法用来开始监听目标元素（这里是 sidebar）的尺寸变化,只要 sidebar 的宽度或高度发生变化，回调函数会被触发
        resizeObserver.value.observe(sidebar);
    }
})
onBeforeUnmount(() => {
    if (resizeObserver.value) {
        // 组件销毁时移除监听
        resizeObserver.value.disconnect();
    }
});
</script>
<template>
    <div class="mr-12 ">
        <div id=slide class="w-56 h-full transition-all duration-300 overflow-hidden" :class="{ closeSlide: !isOpen }">
            <!--  -->
            <div class="w-56 h-full fixed left-0 top-0  bg-[#1d1d1d] text-[#d2d2d2] transition-all duration-300 overflow-hidden"
                :class="{ closeSlide: !isOpen }">
                <div class="flex justify-between  p-3 items-center">
                    <div v-if="isOpen" class="text-xl font-semibold cursor-pointer h-10"
                        @click="useRouter().push('/blogger')">
                        <span v-if="store.blogInfo?.bloggerName">{{ store.blogInfo?.bloggerName }}</span>
                        <span v-else>加载中...</span>
                    </div>
                    <div class="text-xl font-semibold cursor-pointer transition-transform duration-300"  :class="{'rotate-180':isOpen}" @click="toggleOpen">
                        <ArrowLeft />
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
