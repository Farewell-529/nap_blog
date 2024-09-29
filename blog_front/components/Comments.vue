<script setup lang="ts">
import { saveCommentstApi, commentstListApi } from "~/api/conmments";
import { toast } from 'vue-sonner'
import { type Comments, type CommentsRes } from "~/types/Comments";
type Ttarget = {
    targetType: string
    targetId: number
}
const props = defineProps({
    target: {
        type: Object as PropType<Ttarget>,
        required: true
    },
    commentsList: {
        type: Array<Comments>,
        required: true
    }
})
const isReply = ref<number>(0)
const isSend = ref(false)
const commentsShowList = ref<Array<CommentsRes>>([])
const commentsInfo = ref({
    targetId: props.target.targetId,
    targetType: props.target.targetType,
    pid: -1,
    replyId: -1,
    name: '',
    email: '',
    url: '',
    content: ''
});
const mapComments = (comments: any[]) => {
    return comments.map((item: any) => {
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
const getCommentsList = () => {
    if (!props.commentsList || !Array.isArray(props.commentsList)) {
        return;
    }
    commentsShowList.value = mapComments(props.commentsList);
}
const fetchCommentsList = async () => {
    const { data } = await commentstListApi(props.target);
    commentsShowList.value = mapComments(data);
}
const formatDateWithDay = (date: string) => {
    const d = new Date(date);
    const dayNames = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    // 获取日期的年、月、日
    const year = d.getFullYear();
    // 月份是从0开始的，所以需要+1
    const month = d.getMonth() + 1;
    const day = d.getDate();
    // 获取星期几
    const dayOfWeek = dayNames[d.getDay()];
    // 拼接日期和星期几，格式为****年**月*日 星期*
    return `${year}年${month}月${day}日 ${dayOfWeek}`;
};
const addComment = async () => {
    if (!validateRole(commentsInfo.value.name, commentsInfo.value.email, commentsInfo.value.url, commentsInfo.value.content)) {
        return;
    }
    isSend.value = true
    const { code, msg } = await saveCommentstApi(commentsInfo.value)
    if (code != 200) {
        toast.error(msg)
        return
    }
    toast.success("成功发布~")
    await fetchCommentsList()
    isReply.value = 0
    isSend.value = false
    commentsInfo.value.content = ''
    commentsInfo.value.targetId = -1
    commentsInfo.value.pid = -1
}
const validateRole = (name: string, email: string, url: string, content: string) => {
    if (name == '') {
        toast.error("得填写昵称")
        return false
    }
    if (content == '') {
        toast.error("得填写内容")
        return false
    }
    if (email == '') {
        toast.error("得填写邮箱")
        return false
    }
    if (url == '') {
        toast.error("得填写网址")
        return false
    }
    // 邮箱验证的正则表达式
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // URL验证的正则表达式
    const urlRegex = /^https?:\/\/\S+$/;
    if (!emailRegex.test(email)) {
        toast.error("这好像不是一个邮箱")
        return false
    }
    if (!urlRegex.test(url)) {
        toast.error("这好像不是一个链接")
        return false
    }
    return true
}
const replyHandle = (id: number, rootCommentId?: number, replyId?: number) => {
    // 如果当前回复框已经打开，点击同一个评论则收回回复框
    if (isReply.value === id) {
        isReply.value = 0;
    } else {
        // 否则切换当前评论的回复状态
        isReply.value = id;
        commentsInfo.value.pid = rootCommentId !== undefined ? rootCommentId : -1;
        commentsInfo.value.replyId = replyId || -1;
    }
};
watch(() => props.commentsList, (newList) => {
    if (newList && Array.isArray(newList)) {
        getCommentsList();
    }
})
onMounted(() => {
    getCommentsList();
})
</script>
<template>
    <div class="w-[60rem] ml-[3.75rem] ">
        <CommentForm @addComment="addComment" :isSend v-model:commentsInfo="commentsInfo"></CommentForm>
        <div v-for="rootComment in commentsShowList">
            <div class="mt-5 flex items-center ">
                <div class="size-9 ">
                    <img class="rounded-lg" :src="'https://www.gravatar.com/avatar/' + rootComment.avatar" alt="">
                </div>
                <div class="ml-3 flex flex-col">
                    <div>
                        <span class="font-semibold text-[15px] mr-2 ">{{ rootComment.name }}</span>
                        <span class="text-[10px] text-gray-500">{{ rootComment.createDate }}</span>
                    </div>
                    <span
                        class="text-[14px] w-max  text-gray-100 bg-gray-800 rounded-tl-xl rounded-tr-lg rounded-br-lg px-3 py-1">
                        {{ rootComment.content }}
                    </span>
                    <span class="text-[12px] cursor-pointer font-semibold mt-2 select-none"
                        @click="replyHandle(rootComment.id!, rootComment.id)">回复</span>
                </div>
            </div>
            <transition name="CommentForm">
                <CommentForm class="CommentForm" v-show="isReply == rootComment.id" :isSend
                    v-model:commentsInfo="commentsInfo" @addComment="addComment">
                </CommentForm>
            </transition>

            <!-- 子评论 -->
            <div v-for="childComment in rootComment.childComments">
                <div class="mt-3 flex items-center ml-10 ">
                    <div class="size-9">
                        <img class="rounded-lg" :src="'https://www.gravatar.com/avatar/' + childComment.avatar" alt="">
                    </div>
                    <div class="ml-3 flex flex-col">
                        <div>
                            <span class="font-semibold text-[15px]">{{ childComment.name }}</span>
                            <span class="text-[10px] text-gray-500 ml-2">
                                {{ formatDateWithDay(childComment.createDate!) }}
                            </span>
                            <span v-if="childComment.replyName" class="font-semibold text-[14px]">
                                @{{ childComment.replyName }}
                            </span>
                        </div>
                        <span
                            class="text-[14px] w-max text-gray-100 bg-gray-800 rounded-tl-xl rounded-tr-lg rounded-br-lg px-3 py-1">
                            {{ childComment.content }}
                        </span>
                        <span class="text-[12px] cursor-pointer font-semibold mt-2 select-none"
                            @click="replyHandle(childComment.id!, rootComment.id, childComment.id)">
                            回复
                        </span>
                    </div>
                </div>
                <transition name="CommentForm">
                    <CommentForm class="CommentForm" v-show="isReply == childComment.id" :isSend
                        v-model:commentsInfo="commentsInfo" @addComment="addComment">
                    </CommentForm>
                </transition>
            </div>
        </div>
    </div>
</template>
<style scoped>
.CommentForm {
    max-height: 400px;
    transition: height 0.3s ease-in;
    overflow: hidden;
}

.CommentForm-enter-active,
.CommentForm-leave-active {
    transition: all 0.3s ease-in
}

.CommentForm-leave-to,
.CommentForm-enter-from {
    max-height: 0px !important;
}

.CommentForm-leave-to,
.CommentForm-enter-to {
    max-height: 400px;
}
</style>