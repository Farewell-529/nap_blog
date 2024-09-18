<script setup lang="ts">
import { saveCommentstApi } from "~/api/conmments";
import { toast } from 'vue-sonner'

const props = defineProps({
    articleId: Number,
    pid: {
        type: Number,
        default: -1
    },
    target: {
        type: String,
        default: null
    }
});
const emit = defineEmits(['addComment']);
const commentsInfo = ref({
    articleId: props.articleId,
    pid: props.pid,
    target: props.target,
    name: '',
    email: '',
    url: '',
    content: ''
});

const addComment = async () => {
    await emit('addComment', commentsInfo.value)
    commentsInfo.value.content = ''

};
</script>

<template>
    <form class="w-full mt-10">
        <div class="flex justify-between gap-20 mb-5">
            <input class="input-style" placeholder="昵称*" v-model="commentsInfo.name" />
            <input class="input-style" placeholder="邮箱*" v-model="commentsInfo.email" />
            <input class="input-style" placeholder="网址*" v-model="commentsInfo.url" />
        </div>
        <div class="rounded-md border-solid border-2 border-gray-800 h-36 p-2 relative">
            <textarea class="size-full resize-none h-28 focus:outline-none" placeholder="说些什么吧"
                v-model="commentsInfo.content"></textarea>
            <button type="button" class="send" @click="addComment">
                发送
            </button>
        </div>
    </form>
</template>
<style scoped>
.input-style {
    border: 2px solid #1f2937;
    padding: 0.25rem;
    border-radius: 0.375rem;
    width: 14rem;
    outline: none;
}

.send {
    position: absolute;
    right: 0.5rem;
    bottom: 0.5rem;
    background: black;
    padding: 0.2rem;
    border-radius: 5px;
    color: white;
    transition: all 0.2s;
    font-size: 15px;
}

.send:hover {
    transform: scale(1.1);
}

.send:active {
    transform: scale(0.9);
}
</style>
