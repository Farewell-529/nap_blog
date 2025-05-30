<script setup lang="ts">
import { type Comments } from "~/types/Comments";
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiClose,mdiSend  } from '@mdi/js';
import "~/assets/css/main.css"
const isClient = ref(false);
const props = defineProps({
    isSend: Boolean,
    replyName: String
});
const emit = defineEmits(['addComment', 'replyHandle']);
const commentsInfo = defineModel<Comments>('commentsInfo', {
    required: true,
    default: { content: '' }
});
const maxLength = 500; // 最大字数
const currentLength = computed(() => commentsInfo.value.content.length); // 当前字数
const isReply = defineModel('isReply');
const addComment = async () => {
    await emit('addComment')
};
const clickReply = () => {
    isReply.value = 0
}
const saveToLocalStorage =async () => {
        localStorage.setItem('comment-name', commentsInfo.value.name);
        localStorage.setItem('comment-email', commentsInfo.value.email);
        localStorage.setItem('comment-url', commentsInfo.value.url);
};
onMounted(() => {
    isClient.value = true
});
</script>

<template>
    <form v-if="isClient" class="w-full mt-2 p-3 overflow-hidden font-mono">
        <div class="flex justify-between gap-5 mb-5">
            <input class="input-style" type="text" placeholder="昵称*" @input="saveToLocalStorage"
                v-model="commentsInfo.name" />
            <input class="input-style" type="email" placeholder="邮箱*" @input="saveToLocalStorage"
                v-model="commentsInfo.email" />
            <input class="input-style" type="url" placeholder="网址*" @input="saveToLocalStorage"
                v-model="commentsInfo.url" />
        </div>
        <div class="content">
            <textarea class="w-full h-[80%] resize-none focus:outline-none bg-transparent font-mono" placeholder="说些什么吧"
                v-model="commentsInfo.content">
            </textarea>
            <div class="w-full flex justify-between ">
                <div v-show="replyName" class="flex items-center  px-2 rounded-sm cursor-pointer" @click="clickReply"
                    style="background-color: var(--btn-bg-color);">
                    <span class="text-sm max-w-52 whitespace-nowrap overflow-hidden text-ellipsis"
                        style="color: var(--btn-text-color);">
                        回复@{{ replyName }}
                    </span>
                    <svg-icon type="mdi" :path="mdiClose" class="w-4" style="color: var(--btn-text-color);" />
                </div>
                <span >
                    {{ currentLength }}/{{ maxLength }}
                </span>
                <div class="ml-auto w-max flex">
                    <button v-if="!isSend" type="button" class="send" @click="addComment">
                        发送 
                    </button>
                    <button v-else type="button" class="send">
                        发送中...
                    </button>
                </div>
            </div>
        </div>
    </form>
</template>
<style scoped>
.input-style {
    border: 1px solid var(--input-border-color);
    color: var(--text-color);
    padding: 0.25rem;
    border-radius: 0.375rem;
    background: var(--input-bg-color);
    width: 20rem;
    outline: none;
    transition: all 0.3s;
    box-sizing: border-box;
}

.input-style:focus {
    border: 1px solid var(--input-border-foucs-color);
    box-shadow: 0 4px 8px var(·input-shadow-foucs-color);
}

.send {
    background: var(--btn-bg-color);
    padding: 0.2rem;
    border-radius: 5px;
    color: var(--btn-text-color);
    font-size: 15px;
    transition: all 0.3s;
    cursor: pointer;
}

.content {
    height: 10rem;
    padding: 0.8rem;
    position: relative;
    border-radius: 0.5rem;
    background: var(--input-bg-color);
    transition: all 0.3s;
    border: 1px solid transparent;
    box-sizing: border-box;
}

.content:focus-within {
    border: 1px solid var(--input-border-foucs-color);
    box-shadow: 0 4px 12px var(--input-shadow-foucs-color);
}

content::placeholder {
    color: var(--input-placeholder-color);
}

.content textarea::-webkit-scrollbar-track {
    background-color: transparent;
}

.send:hover {
    transform: scale(1.1);
}

.send:active {
    transform: scale(0.9);
}
</style>
