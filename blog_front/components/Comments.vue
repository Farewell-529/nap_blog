<script setup lang="ts">
import { saveCommentstApi, commentstListApi } from "~/api/conmments";
import { toast } from 'vue-sonner'
const props = defineProps({
    articleId: Number
})
const pid = ref(-1)
const target = ref('')
const isReply = ref<Record<number, boolean>>({}); // 每个评论的回复状态
const commentsList = ref()
const getCommentsList = async () => {
    const { data } = await commentstListApi(props.articleId!)
    commentsList.value = data.map((item: any) => {
        return {
            id: item.id,
            name: item.name,
            content: item.content,
            childComments: item.childComments,
            avatar: item.avatar,
            createDate: formatDateWithDay(item.createDate)
        }
    })
}
const formatDateWithDay = (date: string) => {
    const d = new Date(date);
    const dayNames = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    // 获取日期的年、月、日
    const year = d.getFullYear();
    const month = d.getMonth() + 1; // 月份是从0开始的，所以需要+1
    const day = d.getDate();
    // 获取星期几
    const dayOfWeek = dayNames[d.getDay()];
    // 拼接日期和星期几，格式为****年**月*日 星期*
    return `${year}年${month}月${day}日 ${dayOfWeek}`;
};
const addComment = async (commentsInfo: any) => {
    const { code } = await saveCommentstApi(commentsInfo)
    if (code != 200) return
    toast.success("成功发布~")
    getCommentsList()
    isReply.value={}
}
const replyHandle = (id: number, rootCommentId?: number, targetName ?: string) => {
    // 切换当前评论的回复状态
    isReply.value = { [id]: !isReply.value[id] };
    if (rootCommentId != null) pid.value = rootCommentId
    if (targetName  != null) target.value = targetName 
    console.log(targetName);
};

onMounted(() => {
    getCommentsList()
})
</script>
<template>
    <div>
        <CommentForm @addComment="addComment" :articleId></CommentForm>
        <div v-for="rootComment in commentsList">
            <div class="mt-8 flex items-start ">
                <div class="rounded-full size-10 bg-black">
                </div>
                <div class="ml-3 flex flex-col">
                    <div>
                        <span class="font-semibold text-[15px] mr-2">{{ rootComment.name }}</span>
                        <span class="text-[10px] text-gray-500">{{ rootComment.createDate }}</span>
                    </div>
                    <span class="text-[13px] font-bold">{{ rootComment.content }}</span>
                    <span class="text-[12px] cursor-pointer font-semibold mt-2"
                        @click="replyHandle(rootComment.id, rootComment.id)">回复</span>
                </div>
            </div>
            <CommentForm v-if="isReply[rootComment.id]" :articleId :pid @addComment="addComment"></CommentForm>
            <!-- 子评论 -->
            <div v-for="childComment in rootComment.childComments" class="mt-5 flex items-start ml-4">
                <div class="rounded-full size-10 bg-black">
                </div>
                <div class="ml-3 flex flex-col">
                    <div>
                        <span class="font-semibold text-[15px]">{{ childComment.name }}</span>
                        <span class="text-[10px] text-gray-500 ml-2">{{ formatDateWithDay(childComment.createDate)
                            }}</span>
                        <span class="font-semibold text-[14px]">@{{ childComment.target }}</span>
                    </div>
                    <span class="text-[13px] font-bold">{{ childComment.content }}</span>
                    <span class="text-[12px] cursor-pointer font-semibold mt-2"
                        @click="replyHandle(childComment.id, rootComment.id, childComment.name)">回复</span>
                    <CommentForm v-if="isReply[childComment.id]" :articleId :pid :target @addComment="addComment">
                    </CommentForm>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped></style>