<script setup lang="ts">
import { ArrowLeft, ArrowRight, Home, Newspaper, Tags, BookOpenCheck, MessageSquareMore, LayoutDashboard, Aperture, UserRound } from 'lucide-vue-next'
import { userStore } from '~/store/userInfo'
import { getUserInfoApi } from '~/api/user'
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
        url: '/user',
        icon: UserRound
    }
]
const isOpen = ref(true)
const toggleOpen = () => {
    isOpen.value = !isOpen.value
}
const store = userStore()
const getUserInfo = async () => {
    const res = await getUserInfoApi()
    if (res.data == null) {
        useRouter().push('/login/')
        return
    }
    store.setUser(res.data)
};
onMounted(() => {
    getUserInfo()
})
</script>
<template>
    <div>
        <div class="fixed  top-4 z-[99] hover:cursor-pointer" @click="toggleOpen">
            <ArrowLeft class="fixed left-44" :size="25" v-if="isOpen"/>
            <ArrowRight class="fixed left-4" :size="25" v-else />
        </div>
        <Transition name="slide" appear>
            <div v-if="isOpen" class="w-60 h-[100%] ">
                <div class="w-60 h-full fixed top-0 left-0 border-r-[3px] border-solid border-black ">
                    <div class="flex justify-between p-3">
                        <div class="text-xl font-semibold hover:cursor-pointer" @click="useRouter().push('/user')">
                            <span v-if="store.user?.username">{{ store.user?.username }}</span>
                            <span v-else>加载中...</span>
                        </div>

                    </div>
                    <div v-for="(item, index) in list" :key="index">
                        <NuxtLink :to="item.url" class=" flex gap-8 items-center my-6 text-l 
            font-semibold  hover:bg-gray-200 transition-all duration-300 py-4 pl-3">
                            <span>
                                <component :is="item.icon" />
                            </span>
                            <span>{{ item.name }}</span>
                        </NuxtLink>
                    </div>
                </div>
            </div>
        </Transition>
    </div>
</template>
<style scoped>
.slide-enter-active,
.slide-leave-active {
    transition: all 0.2s ease;
}

.slide-enter-from,
.slide-leave-to {
    /* margin-right: 0; */
    width: 0;
    transform: translateX(calc(-100% - 18rem));
}


/* 当前路径与 nuxt-link 对应的路径完全一致 */
.router-link-exact-active {
    background-color: rgb(55, 55, 55);
    color: white;
}
</style>
