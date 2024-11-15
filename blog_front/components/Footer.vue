<script setup lang="ts">
// @ts-ignore
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiWhiteBalanceSunny, mdiMoonFull } from '@mdi/js';
import { themeStore } from "~/store/theme";
const theme = themeStore()
const updateClass = () => {
    if (theme.isDark) {
        document.documentElement.classList.add("dark");
    } else {
        document.documentElement.classList.remove("dark");
    }
};
const clickBtn = () => {
    theme.toggleTheme()
    updateClass()
}
onMounted(() => {
    theme.initTheme(); // 初始化主题
    updateClass(); // 根据当前主题设置类
});
</script>
<template>
    <div>
        <div class="fixed bottom-5 right-5">
            <span @click="clickBtn">
                <svg-icon id="svgIcon" v-if="theme.isDark" size="35" type="mdi" :path="mdiMoonFull" class="cursor-pointer icon" />
                <svg-icon id="svgIcon" v-else size="35" type="mdi" :path="mdiWhiteBalanceSunny" class="cursor-pointer icon " />
            </span>
        </div>
    </div>
</template>
<style scoped>
.icon {
    transition: transform 0.3s ease;
}
.icon:hover {
    transform: rotate(180deg) scale(1.1);
}

.icon:active {
    transform: scale(0.9);
}
</style>