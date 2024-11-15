<script setup lang="ts">
import type { User } from '~/types/User';
import { loginApi, validateEmailApi, verifyCodeApi, resetPasswordApi } from '~/api/login'
import { getBloggerInfoApi } from '~/api/blog'
import { UserRound } from 'lucide-vue-next'
import { blogInfoStore } from '~/store/blogInfo'
const store = blogInfoStore()
const loading = ref(false)
const loginColor = '#ffffff'
const currentHandle = ref<'login' | 'valideEmail' | 'valideCode' | 'resetPassword'>('login')
const loginForm = ref<User>({
    username: '',
    password: '',
})
const emailForm = ref({
    username: '',
    email: ''
})
const codeForm = ref('')
const newPasswordForm = ref('')
const indeterminate = ref(false)
const { $toast } = useNuxtApp()

const handleApiCall = async (apiFunc: Function, payload: any, nextStep?: typeof currentHandle.value) => {
    loading.value = true
    const { code, msg } = await apiFunc(payload)
    loading.value = false
    if (code !== 200) return $toast.error(msg)
    $toast.success(msg)
    if (nextStep) currentHandle.value = nextStep
}
const logingBtn = async () => {
    loading.value = true
    if (loginForm.value.username == "" || loginForm.value.password == "") {
        $toast.error("账号或密码不能为空")
        loading.value = false
        return
    }
    const res = await loginApi(loginForm.value)
    if (res.code !== 200) {
        $toast.error("账号或密码错误")
        loginForm.value.username = ''
        loginForm.value.password = ''
        loading.value = false
        return
    }
    localStorage.setItem("token", res.data)
    $toast.success("登录成功噜~")
    loginForm.value.username = ''
    loginForm.value.password = ''
    const {data}=await  getBloggerInfoApi()
    store.setBlogInfo(data)
    useRouter().push('/')
}

const forgetPwdBtn = () => {
    currentHandle.value = 'valideEmail'
}
const keyUpHandler = (e: KeyboardEvent) => {
    // 只在登录窗口时处理回车事件
    if (currentHandle.value === 'login' && e.code == "Enter") {
        logingBtn();
    }
};

const valideEmailBtn = async () => {
    if (emailForm.value.username == null || emailForm.value.username == '') {
        $toast.error("请输入账号")
        return
    }
    if (emailForm.value.email == null || emailForm.value.email == '') {
        $toast.error("请输入邮箱")
        return
    }
    indeterminate.value = true
    handleApiCall(validateEmailApi,emailForm.value,'valideCode').then(()=>{
        indeterminate.value = false
    })
  
}
const valideCodeBtn = async () => {
    handleApiCall(verifyCodeApi,codeForm.value,'resetPassword')
  
}
const resetPasswordBtn = async () => {
    handleApiCall(resetPasswordApi,newPasswordForm.value,'login')
}
const resetSendCode = async () => {
    handleApiCall(validateEmailApi,emailForm.value,'login').then(()=>{
        indeterminate.value = false
    })
}
const handleBack = (targetStep: typeof currentHandle.value) => currentHandle.value = targetStep
const backToEmail = () => {
    currentHandle.value = 'valideEmail'
    codeForm.value = ''
}
onMounted(() => window.addEventListener('keyup', keyUpHandler))
onUnmounted(() => window.removeEventListener('keyup', keyUpHandler))
</script>
<template>
    <div class="size-full bg-gray-950  fixed z-[999]">
        <!-- login -->
        <v-form class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32"
            v-if="currentHandle == 'login'">
            <div class="flex items-center gap-2">
                <span class="text-xl font-semibold">
                    登录
                </span>
                <UserRound :size="16" />
            </div>
            <v-row dense>
                <v-col cols="12">
                    <v-text-field v-model="loginForm.username" label="账号" required variant="solo" class="mt-10"
                        hint="输入账号" autocomplete="username"></v-text-field>
                </v-col>
                <v-col cols="12">
                    <v-text-field v-model="loginForm.password" label="密码" type="password" required variant="solo"
                        class="" hint="输入密码" autocomplete="current-password"></v-text-field>
                </v-col>
                <v-col cols="8">
                    <v-btn :color="loginColor" class="mt-3 text-white bg-black" :loading size="large"
                        variant="elevated" block @click="logingBtn">
                        登录
                    </v-btn>
                </v-col>
                <v-col cols="4">
                    <v-btn :color="loginColor" class="mt-3 text-black bg-white" size="large" variant="elevated" block
                        @click="forgetPwdBtn">
                        忘记密码?
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>
        <v-form class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32"
            v-if="currentHandle == 'valideEmail'">
            <div class="flex items-center gap-2">
                <span class="text-xl font-semibold">
                    忘记密码
                </span>
                <UserRound :size="16" />
            </div>
            <v-row dense>
                <v-col cols="12">
                    <v-text-field v-model="emailForm.username" label="账号" required variant="solo" class="mt-10"
                        hint="输入账号"></v-text-field>
                </v-col>
                <v-col cols="12">
                    <v-text-field v-model="emailForm.email" label="邮箱" required variant="solo"
                        hint="输入邮箱"></v-text-field>
                </v-col>
                <v-col cols="6">
                    <v-btn class="my-2 "  size="large" variant="elevated" block
                        @click="handleBack('login')">
                        返回
                    </v-btn>
                </v-col>
                <v-col cols="6">
                    <v-btn class="my-2 text-white bg-black" :loading size="large" variant="elevated" block
                        @click="valideEmailBtn">
                     发送验证码
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>
        <v-form class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32"
            v-if="currentHandle == 'valideCode'">
            <div class="flex flex-col items-center justify-center gap-2">
                <span class="text-xl font-semibold ">
                    验证码
                </span>
                <span class="text-xs text-gray-500">
                    已向{{ emailForm.email }}发送了验证码
                </span>
            </div>
            <v-row dense>
                <v-col cols="12">
                    <v-otp-input v-model="codeForm"></v-otp-input>
                </v-col>
                <div class="text-[13px]  flex justify-center w-full text-center">
                    <span class="w-[320px]">
                        如果在收件箱中未看到该邮件，请查看垃圾邮件文件夹和广告邮件栏。如果仍未看到，要求
                        <b class="cursor-pointer" @click="resetSendCode">重新发送</b>。
                    </span>
                </div>
                <v-col cols="6">
                    <v-btn class="my-2 " size="large" variant="elevated" block @click="backToEmail">
                        返回
                    </v-btn>
                </v-col>
                <v-col cols="6">
                    <v-btn class="my-2 text-white bg-black" :loading size="large" variant="elevated" block
                        @click="valideCodeBtn">
                        验证
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>
        <v-form class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32"
            v-if="currentHandle == 'resetPassword'">
            <div class="flex items-center gap-2">
                <span class="text-xl font-semibold">
                    重置密码
                </span>
                <UserRound :size="16" />
            </div>
            <v-row dense>
                <v-col cols="12">
                    <v-text-field v-model="newPasswordForm" label="输入新的密码" required variant="solo" class="mt-10"
                        hint="输入邮箱中的验证码"></v-text-field>
                </v-col>
                <v-col cols="6">
                    <v-btn class="my-2 "  size="large" variant="elevated" block @click="backToEmail">
                        返回
                    </v-btn>
                </v-col>
                <v-col cols="6">
                    <v-btn class="my-2 text-white bg-black" :loading size="large" variant="elevated" block
                        @click="resetPasswordBtn">
                        重置
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>
    </div>
</template>
<style scoped></style>