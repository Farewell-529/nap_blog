<script setup lang="ts">
import { friendListApi } from "~/api/friend";
import { type Friend } from "~/types/Friend";
import { type Comments, type CommentsRes } from "~/types/Comments";

const target = {
    targetType: 'friends',
    targetId: 0
}
const avatarUrl = 'http://localhost:8080/static/avatar/252544f4e0634e5aa66205cee2521d08.jpg'
const friendList = ref<Array<Friend>>()
const commentsList = ref<Array<Comments>>([]);
const getFriendList = async () => {
    const { data } = await friendListApi()
    friendList.value = data.friendList
    commentsList.value=data.commentsList
}
onMounted(() => {
    getFriendList()
})
</script>
<template>
    <div>
        <div class="font-extrabold  mb-8 text-center">
            <h1 class="text-4xl mb-4">
                朋友们
            </h1>
            <h2 class="text-lg">
                欢迎交换友链~，信息如有变动，请联系我更改
            </h2>
            <h2 class="text-lg">
                以下是我的友链列表，列表随机排序
            </h2>
        </div>
        <div class="flex  flex-wrap w-[50rem] justify-between">
            <div v-for="friend in friendList">
                <a :href="friend.url" role="link" target="_blank" rel="noopener noreferrer" class="friend">
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
        <div class="font-mono flex flex-col flex-wrap w-[50rem] p-3 ">
            <h1 class="text-3xl mb-2">博客信息:</h1>
            <p class="mb-2">站点名称:<span class="bg-green-500/40 p-1 rounded-sm">Nap或Nap's blog</span></p>
            <p class="mb-2">站点介绍:<span class="bg-green-500/40 p-1 rounded-sm">去码头整点薯条</span></p>
            <p class="mb-2">站点链接:<span class="text-green-500">http://localhost:3001/friend</span></p>
            <p class="mb-2">站点头像/Logo:<a role="link" target="_blank" rel="noopener noreferrer"
                    class="text-green-500 cursor-pointer" :href="avatarUrl">
                    Nap
                </a></p>
        </div>
        <div class="font-mono flex flex-col flex-wrap w-[50rem] p-3 ">
            <h1 class="text-3xl mb-2">申请条件:</h1>
            <p>1.先友后链，申请前先添加Nap的友链</p>
            <p>2.网站要稳定运行哦，不希望添加没多久后网站就失联了（各种风格的博客都欢迎）</p>
            <p>3.不接受含有暴力、色情、政治信息的网站</p>
        </div>
        <div class="font-mono flex flex-col flex-wrap w-[50rem] p-3 ">
            <h1 class="text-3xl mb-2">申请格式:</h1>
            <p class="mb-2 "><span class="bg-green-500/40 p-1 rounded-sm">站点名称:</span>
                最好不要超过20个字符。内容不应该出现歧视性、侮辱性、色情、暴力、政治等不适合展示元素</p>
            <p class="mb-2"><span class="bg-green-500/40 p-1 rounded-sm">站点介绍:</span>
                一句话介绍你的站点，内容不要超过30个字符。另外内容也不应该出现以上不适合展示的元素</p>
            <p class="mb-2"><span class="bg-green-500/40 p-1 rounded-sm">站点链接:</span>
                你的网站链接</p>
            <p class="mb-2"><span class="bg-green-500/40 p-1 rounded-sm">站点头像/Logo:</span>
                你的头像或Logo图片的链接</p>
        </div>
        <Comments :target :commentsList></Comments>
        <div class="h-40"></div>
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
    transition: transform 0.3s cubic-bezier(0.22, 1.61, 0.36, 1);
    /* 果冻弹跳动画 */
    z-index: -1;
}

.friend:hover::before {
    transform: scale(1);
    opacity: 1;
}
</style>