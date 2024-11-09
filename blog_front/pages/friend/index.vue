<script setup lang="ts">
import { friendListApi } from "~/api/friend";
import { type Friend } from "~/types/Friend";
const friendList = ref<Array<Friend>>()
const getFriendList = async () => {
    const { data } = await friendListApi()
    friendList.value = data
}
onMounted(() => {
    getFriendList()
})
</script>
<template>
    <div >
        <div class="font-extrabold  mb-8 text-center">
            <h1 class="text-4xl mb-4">
                朋友们
            </h1>
            <h2 class="text-lg">
                欢迎交换友链~，信息如有变动，请联系我更改
            </h2>
        </div>
        <div class="flex  flex-wrap w-[50rem] justify-between">
            <div v-for="friend in friendList">
                <a :href="friend.url" role="link" target="_blank"  rel="noopener noreferrer" class="friend">
                    <span class="rounded-lg border-[3px] " style="border-color: var( --btn-border-color);">
                        <img :src="friend.avatarUrl" alt="" class="size-16 rounded-lg">
                    </span>
                    <span class="text-lg font-medium ">
                        {{ friend.friendName }}
                    </span>
                    <span class=" text-balance break-all text-center text-sm " style="color: var(--minor-text-color);">
                        {{ friend.description }}
                    </span>
                </a>
            </div>
        </div>
    </div>
</template>
<style scoped>
.friend {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 14rem;
    cursor: pointer;
    padding: 2rem 0;
    position: relative; 
    z-index: 0; 
}

.friend::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(63, 63, 63, 0.27); 
    border-radius: 0.3rem;
    transform: scale(0.8); 
    opacity: 0;
    transition: transform 0.3s cubic-bezier(0.22, 1.61, 0.36, 1); /* 果冻弹跳动画 */
    z-index: -1; 
}

.friend:hover::before {
    transform: scale(1); 
    opacity: 1;
}
</style>