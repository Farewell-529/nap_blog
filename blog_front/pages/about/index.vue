<script setup lang="ts">
import '~/assets/css/markdown.css';
import 'github-markdown-css/github-markdown.css';
import { useMarkdown } from '~/utils/useMarkdown';
import { type User } from "~/types/User";
import { userStore } from "~/store/userInfo";
const store = userStore()
const userInfo = ref<User>({
    motto: '',
    avatar: '',
    bio: ''
})
const getUserInfo = async () => {
    if (store.user) {
        userInfo.value = { ...store.user }
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
                :src="useRuntimeConfig().public.baseURL + '/static/user/avatar/' + userInfo.avatar" alt="">
            <span class="text-xs text-gray-500 font-bold">{{ userInfo.motto }}</span>
        </div>
        <Article :content="userInfo.bio">
        </Article>
    </div>
</template>
<style scoped></style>