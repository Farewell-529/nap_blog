<script setup lang="ts">
import { saveCommentstApi, commentstListApi } from "~/api/conmments";
import { toast } from 'vue-sonner'
import { type Comments, type CommentsRes } from "~/types/Comments";
import "~/assets/css/main.css"
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
const replyName = ref('')
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
// 加载 localStorage 中的数据
const loadCommentsInfo = () => {
    // 设置到 commentsInfo 对象
    commentsInfo.value.name = localStorage.getItem('comment-name') || '';
    commentsInfo.value.email = localStorage.getItem('comment-email') || '';
    commentsInfo.value.url = localStorage.getItem('comment-url') || '';
};
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
        isSend.value = false
        return
    }
    toast.success(msg)
    await fetchCommentsList()
    isReply.value = 0
    commentsInfo.value.content = ''
    commentsInfo.value.replyId = -1
    commentsInfo.value.pid = -1
    isSend.value = false
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


const replyHandle = async (id: number, rootCommentId?: number, replyId?: number) => {
    // 如果当前回复框已经打开，点击同一个评论则收回回复框
    if (isReply.value === id) {
        isReply.value = 0;
    } else {
        // 否则切换当前评论的回复状态
        isReply.value = id;
        commentsInfo.value.pid = rootCommentId !== undefined ? rootCommentId : -1;
        const target = document.getElementById(`CommentForm-${id}`)
        setTimeout(() => {
            if (target) {
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 100)

    }
    // 查找被回复者的名字
    if (!replyId) {
        commentsInfo.value.replyId = -1
        replyName.value = commentsShowList.value.find(comment => comment.id === rootCommentId)?.name || ''
        return
    }
    commentsInfo.value.replyId = replyId
    const comment = commentsShowList.value
        .flatMap(comment => comment.childComments) // 先展平所有子评论
        .find(childComment => childComment?.id === replyId);
    replyName.value = comment!.name || '';
};
watch(() => props.commentsList, (newList) => {
    if (newList && Array.isArray(newList)) {
        getCommentsList();
    }
})

watch(() => isReply.value, (newVal) => {
    if (newVal == 0) {
        setTimeout(() => {
            const target = document.getElementById("defaultCommentForm")
            if (target) {
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 100)
    }
});

onMounted(() => {
    getCommentsList();
    loadCommentsInfo();

})
</script>
<template>
    <div class="w-[60rem] ml-[3.75rem] ">
        <CommentForm id="defaultCommentForm" v-show="isReply == 0" @addComment="addComment" :isSend
            v-model:commentsInfo="commentsInfo">
        </CommentForm>

        <div v-for="rootComment in commentsShowList">
            <div class="mt-5 flex items-center ">
                <div class="size-10 ">
                    <img class="rounded-lg"
                        :src="'https://www.gravatar.com/avatar/' + rootComment.avatar + '?d=mysteryman'" alt="">
                </div>
                <div class="ml-3 flex flex-col">
                    <div>
                        <span
                            class="font-semibold text-sm mr-2 inline-block max-w-52 whitespace-nowrap overflow-hidden text-ellipsis">
                            {{ rootComment.name }}
                        </span>
                        <span class="text-[10px] " style="color: var(--minor-text-color);">{{ rootComment.createDate }}
                        </span>
                    </div>
                    <span class="text-sm w-max   rounded-tl-lg rounded-tr-lg rounded-br-lg px-3 py-2 
                         max-w-[50rem]"
                        style="color: var(---comment-content-color); background-color: var(---comment-bg-color)">
                        {{ rootComment.content }}
                    </span>
                    <span class="text-[12px] cursor-pointer font-semibold mt-2 select-none"
                        @click="replyHandle(rootComment.id!, rootComment.id)">回复</span>
                </div>
            </div>
            <CommentForm :id="'CommentForm-' + rootComment.id" v-show="isReply == rootComment.id" :isSend :replyName
                v-model:commentsInfo="commentsInfo" v-model:isReply="isReply" @addComment="addComment"
                @replyHandle=replyHandle>
            </CommentForm>

            <!-- 子评论 -->
            <div v-for="childComment in rootComment.childComments">
                <div class="mt-3 flex items-center ml-10 ">
                    <div class="size-9">
                        <img class="rounded-lg"
                            :src="'https://www.gravatar.com/avatar/' + childComment.avatar + '?d=mysteryman'" alt="">
                    </div>
                    <div class="ml-3 flex flex-col">
                        <div>
                            <span
                                class="font-semibold text-sm inline-block max-w-52 whitespace-nowrap overflow-hidden text-ellipsis">
                                {{ childComment.name }}
                            </span>
                            <span class="text-[10px]  ml-2" style="color: var(--minor-text-color);">
                                {{ formatDateWithDay(childComment.createDate!) }}
                            </span>
                            <span v-if="childComment.replyName" class="font-semibold text-[14px]" style="color: var(--text-color);">
                                @{{ childComment.replyName }}
                            </span>
                        </div>
                        <span class="text-sm w-max  rounded-tl-lg rounded-tr-lg rounded-br-lg px-3 py-2 
                            max-w-[47rem]"
                            style="color: var(---comment-content-color); background-color: var(---comment-bg-color)">
                            {{ childComment.content }}
                        </span>
                        <span class="text-[12px] cursor-pointer font-semibold mt-2 select-none"
                            @click="replyHandle(childComment.id!, rootComment.id, childComment.id)">
                            回复
                        </span>
                    </div>
                </div>
                <CommentForm :id="'CommentForm-' + childComment.id" v-show="isReply == childComment.id" :isSend
                    :replyName v-model:commentsInfo="commentsInfo" v-model:isReply="isReply" @addComment="addComment"
                    @replyHandle=replyHandle>
                </CommentForm>
            </div>
        </div>
    </div>
</template>
<style scoped>
/* .CommentForm-enter-active,
.CommentForm-leave-active {
    transition: opacity 0.1s ease-in;
}

.CommentForm-leave-to,
.CommentForm-enter-from {
    opacity: 0;
}

.CommentForm-leave-from,
.CommentForm-enter-to {
    opacity: 1;
    overflow: hidden;
} */
</style>