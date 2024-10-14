<script setup lang="ts">
import { type Comments } from "~/types/Comments";
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiClose } from '@mdi/js';
const props = defineProps({
    isSend: Boolean,
    replyName: String
});
const emit = defineEmits(['addComment', 'replyHandle']);
const commentsInfo = defineModel<Comments>('commentsInfo', {
    required: true,
});
const isReply = defineModel('isReply');
const addComment = async () => {
    await emit('addComment')
};
const clickReply = () => {
    isReply.value = 0
    // emit('replyHandle')
}
const saveToLocalStorage = () => {
    if (commentsInfo.value.name) {
        localStorage.setItem('comment-name', commentsInfo.value.name);
    }
    if (commentsInfo.value.email) {
        localStorage.setItem('comment-email', commentsInfo.value.email);
    }
    if (commentsInfo.value.url) {
        localStorage.setItem('comment-url', commentsInfo.value.url);
    }
};
</script>

<template>
    <form class="w-full mt-2 p-3 overflow-hidden">
        <div class="flex justify-between gap-5 mb-5">
            <input class="input-style" type="text" placeholder="昵称*" @input="saveToLocalStorage"
                v-model="commentsInfo.name" />
            <input class="input-style" type="email" placeholder="邮箱*" @input="saveToLocalStorage"
                v-model="commentsInfo.email" />
            <input class="input-style" type="url" placeholder="网址*" @input="saveToLocalStorage"
                v-model="commentsInfo.url" />
        </div>
        <div class="content">
            <textarea class="w-full h-[80%] resize-none focus:outline-none " placeholder="说些什么吧" v-model="commentsInfo.content">
            </textarea>
            <div class="w-full flex justify-between ">
                <div v-show="replyName" class="flex items-center bg-black px-2 rounded-sm cursor-pointer"
                    @click="clickReply">
                    <span class="text-sm text-white max-w-52 whitespace-nowrap overflow-hidden text-ellipsis">
                        回复@{{ replyName }}
                    </span>
                    <svg-icon type="mdi" :path="mdiClose" class="w-4" />
                </div>
                <div class="ml-auto w-max">
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
    border: 1px solid #828282;
    padding: 0.25rem;
    border-radius: 0.375rem;
    background: #efefefd3;
    width: 20rem;
    outline: none;
    transition: all 0.3s;
    box-sizing: border-box;
}

.input-style:focus {
    border: 1px solid #2c2c2cd3;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.input-style::placeholder {
    color: rgb(116, 116, 116);
}

.send {
    background: black;
    padding: 0.2rem;
    border-radius: 5px;
    color: white;
    font-size: 15px;
    transition: all 0.3s;
    cursor: pointer;
}

.content {
    height: 10rem;
    padding: 0.8rem;
    position: relative;
    border-radius: 0.5rem;
    background: #d8d8d8d3;
    transition: all 0.3s;
    border: 1px solid transparent;
    box-sizing: border-box;
}

.content:focus-within {
    border: 1px solid #2c2c2cd3;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

content::placeholder {
    color: rgb(116, 116, 116);
}

.send:hover {
    transform: scale(1.1);
}

.send:active {
    transform: scale(0.9);
}
</style>
