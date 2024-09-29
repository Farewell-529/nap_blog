<script setup lang="ts">
import { type Comments } from "~/types/Comments";
const props = defineProps({
    isSend:Boolean
});
const emit = defineEmits(['addComment']);
const commentsInfo = defineModel<Comments>('commentsInfo', {
  required: true,
});
const addComment = async () => {
    await emit('addComment')
};

</script>

<template>
    <form class="w-full mt-10 p-3">
        <div class="flex justify-between gap-5 mb-5">
            <input class="input-style" type="text" placeholder="昵称*" v-model="commentsInfo.name" />
            <input class="input-style" type="email" placeholder="邮箱*" v-model="commentsInfo.email" />
            <input class="input-style" type="url" placeholder="网址*" v-model="commentsInfo.url" />
        </div>
        <div class="content">
            <textarea class="size-full focus:outline-none" placeholder="说些什么吧"
                v-model="commentsInfo.content"></textarea>
            <button v-if="!isSend" type="button" class="send" @click="addComment">
                发送
            </button>
            <button v-else type="button" class="send">
                发送中...
            </button>
        </div>
    </form>
</template>
<style scoped>
.input-style {
    border: 2px solid #828282;
    padding: 0.25rem;
    border-radius: 0.375rem;
    background: #efefefd3;
    width: 20rem;
    outline: none;
    transition: all 0.3s;
}

.input-style:focus {
    border: 2px solid #2c2c2cd3;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.input-style::placeholder {
    color: rgb(116, 116, 116);
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
    transition: all 0.3s;
}

.content {
    height: 9rem;
    padding: 0.5rem;
    resize: none;
    border: 2px solid #828282;
    position: relative;
    border-radius: 0.5rem;
    background: #efefefd3;
    transition: all 0.3s;
}

.content:focus-within {
    border: 2px solid #2c2c2cd3;
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
