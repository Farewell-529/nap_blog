<script setup lang="ts">
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiChevronLeft, mdiChevronRight } from '@mdi/js';
const props = defineProps({
    length: {
        type: Number,
        require: true,
        default: 1
    },
    pageVisible: {
        type: Number,
        require: true,
        default: 5
    }
})
const currentPage = defineModel('currentPage', {
    required: true,
    type: Number,
    default: 1
})
const emit = defineEmits(['update:modelValue'])
const arr = ref<Array<number | string>>([]);
const clickActive = (current: number) => {
    currentPage.value = current
}
const calculatePages = () => {
    const totalPages = props.length;
    const visiblePages = props.pageVisible;
    const middle = Math.ceil(visiblePages / 2)
    if (totalPages <= visiblePages) {
        arr.value = Array.from({ length: totalPages }, (_, index) => index + 1);
    } else {
        if (currentPage.value <= middle) {
            arr.value = [
                ...Array.from({ length: visiblePages - 1 }, (_, index) => index + 1),
                '...',
                totalPages
            ];
        } else if (currentPage.value > totalPages - middle) {
            arr.value = [
                1,
                '...',
                ...Array.from({ length: visiblePages - 1 }, (_, index) => totalPages - visiblePages + 2 + index)
            ];
        } else {
            arr.value = [
                1,
                '...',
                ...Array.from({ length: visiblePages - 3 }, (_, index) => currentPage.value - Math.floor(((visiblePages - 3)/2))  + index),
                '...',
                totalPages
            ];
        }
    }
};

watch(props,()=>{
    calculatePages()
})
watch(currentPage, () => {
    emit('update:modelValue', currentPage)
    calculatePages()
})
calculatePages()
const leftClick = () => {
    if (currentPage.value == 1) return
    currentPage.value = currentPage.value - 1
}
const rightClick = () => {
    if (currentPage.value == props.length) return
    currentPage.value = currentPage.value + 1
}

</script>
<template>
    <div class="flex justify-center items-center">
        <svg-icon type="mdi" :path="mdiChevronLeft" class="w-6 cursor-pointer" @click="leftClick" />
        <div v-for="item in arr" class="size-8 flex items-center justify-center mx-1 rounded-md
        transition-all duration-200  select-none " :class="{ active: currentPage == item,page:item!='...' }"
            @click="item !== '...' && clickActive(item as number)">
            <span class="">{{ item }}</span>
        </div>
        <svg-icon type="mdi" :path="mdiChevronRight" class="w-6  cursor-pointer" @click="rightClick" />
    </div>
</template>
<style scoped>
.active {
    background: #2d2d2d;
    color: rgb(228, 228, 228);
}
.page{
  cursor: pointer;
}
.page:hover{
  background: #cdcdcdb5;
  color: #232323;
}
</style>