<script setup lang="ts">
import type { Account } from '~/types/User';
import { loginApi, registerApi } from '~/api/login'
import { getUserInfoApi } from '~/api/user';
import { UserRound } from 'lucide-vue-next'
import { userStore } from '~/store/userInfo';
const store = userStore()
const loading = ref(false)
const loginColor = '#09917de1'
const registerColor = '#2d9de8e1'
const isRegister = ref(false)
const confirmPassword = ref('')
const loginAccount = ref<Account>({
    username: '',
    password: ''
})
const registerAccount = ref<Account>({
    username: '',
    password: ''
})
const { $toast } = useNuxtApp()
const getUserInfo = async () => {
    const res = await getUserInfoApi()
    store.setUser(res.data)
}
const loging = async () => {
    loading.value = true
    if (loginAccount.value.username == "" || loginAccount.value.password == "") {
        $toast.error("账号或密码不能为空")
        loading.value = false
        return
    }
    const res = await loginApi(loginAccount.value)
    if (res.code !== 200) {
        $toast.error("账号或密码错误")
        loginAccount.value.username = ''
        loginAccount.value.password = ''
        loading.value = false
        return
    }
    localStorage.setItem("token", res.data)
    $toast.success("登录成功噜~")
    // 移除keyup事件监听
    window.removeEventListener('keyup', keyUpHandler)
    loginAccount.value.username = ''
    loginAccount.value.password = ''
    getUserInfo()
    useRouter().push('/')

}

const registerBtn = () => {
    isRegister.value = true
}
const registerHandler = async () => {
    if (registerAccount.value.username == '' || registerAccount.value.password == '') {
        $toast.error("账号或密码不能为空")
        return
    }
    if (registerAccount.value.password != confirmPassword.value) {
        registerAccount.value.password = ''
        confirmPassword.value = ''
        $toast.error("两次密码不一致")
        return
    }
    const res = await registerApi(registerAccount.value)
    registerAccount.value.username = ''
    registerAccount.value.password = ''
    confirmPassword.value = ''
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success("注册成功噜")
    isRegister.value = false
}
const keyUpHandler = (e: KeyboardEvent) => {
    if (e.code == "Enter") {
        isRegister.value ? registerHandler() : loging()
    }

}
onMounted(() => {
    window.addEventListener('keyup', keyUpHandler)
})
</script>
<template>
    <div class="w-screen h-screen  bg-cover bg-[url(~/public/images/logBackground.jpg)]  fixed z-[999]">
        <div class="absolute inset-0 bg-black opacity-40">
        </div>
        <!-- login -->
        <v-form class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32" v-if="!isRegister">
            <div class="flex items-center gap-2">
                <span class="text-xl font-semibold">
                    登录
                </span>
                <UserRound :size="16" />
            </div>
            <v-row dense>
                <v-col cols="12">
                    <v-text-field v-model="loginAccount.username" label="Account" required variant="solo" class="mt-10"
                        hint="输入账号啊嗯" autocomplete="username"></v-text-field>
                </v-col>

                <v-col cols="12">
                    <v-text-field v-model="loginAccount.password" label="Password" type="password" required
                        variant="solo" class="" hint="输入密码啊嗯" autocomplete="current-password"></v-text-field>
                </v-col>

                <v-col cols="12">
                    <v-btn :color="loginColor" class="mt-8 text-white" :loading="loading" size="large" variant="elevated" block
                        @click="loging">
                        Sign In
                    </v-btn>

                </v-col>
                <v-col cols="12">
                    <v-btn class="my-8" :loading="loading" size="large" variant="elevated" block @click="registerBtn">
                        Sign up
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>

        <!-- register -->
        <v-form class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32" v-else>
            <div class="flex items-center gap-2">
                <span class="text-xl font-semibold">
                    注册
                </span>
                <UserRound :size="16" />
            </div>
            <v-row dense>
                <v-col cols="12">
                    <v-text-field v-model="registerAccount.username" label="Enter your Account" required variant="solo"
                        class="mt-10" hint="输入账号啊嗯" autocomplete="username"></v-text-field>
                </v-col>

                <v-col cols="12">
                    <v-text-field v-model="registerAccount.password" label="Password" type="password" required
                        variant="solo" hint="输入密码啊嗯" autocomplete="current-password"></v-text-field>
                </v-col>
                <v-col cols="12">
                    <v-text-field v-model="confirmPassword" label="Confirm Password" type="password" required
                        variant="solo" hint="确认密码" autocomplete="current-password"></v-text-field>
                </v-col>

                <v-col cols="6">
                    <v-btn class="my-2" :color="loginColor" size="large" variant="elevated" block
                        @click="isRegister = false">
                        Back
                    </v-btn>
                </v-col>
                <v-col cols="6">
                    <v-btn class="my-2" :loading="loading" size="large" variant="elevated" block
                        @click="registerHandler">
                        Sign up
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>

    </div>
</template>
<style scoped></style>