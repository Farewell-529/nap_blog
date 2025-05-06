<script setup lang="ts">
import { blogInfoStore } from '../../store/blogInfo'
import { uploadAvatarApi } from '~/api/user'
import { logout } from '~/api/user'
// @ts-ignore
import MarkdownIt from 'markdown-it';
// @ts-ignore
import { createHighlighter } from 'shiki'
import { type User } from "~/types/User";
import 'github-markdown-css/github-markdown-light.css';
import { getAccountApi, checkPasswordApi, editPasswordApi, validateOldEmailApi, validateNewEmailApi, validateCodeApi, editEmail } from "~/api/user";
const store = blogInfoStore()
const blogInfo = ref<any>({})
const logoutDialog = ref(false)
const { $toast } = useNuxtApp()
const accountDialog = ref(false)
const loading = ref(false)
const emailDialog = ref(false)
const account = ref<User>()
const isOldPassword = ref(false)
const isValidateEmail = ref(false)
const codeForm = ref('')
const originalPassword = ref('')
const editPasswordForm = ref({
    username: '',
    password: '',
    confirmPassword: ''
})
const editEmailForm = ref({
    oldEmail: '',
    newEmail: ''
})
const eamilHandle = ref<'validateOldEmail' | 'validateOldEmailCode' | 'validateNewEmail' | 'validateNewEmailCode' | 'editEmail'>('validateOldEmail')
let md: any
const initializeMarkdown = async () => {
    const highlighter = await createHighlighter({
        langs: ['javascript', 'typescript', 'vue', 'java', 'go', 'mermaid'],
        themes: ['tokyo-night']
    })
    md = new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight(value: string, lang: string) {
            try {
                let codeHtml = '';
                if (lang && highlighter.getLoadedLanguages().includes(lang)) {
                    codeHtml = highlighter.codeToHtml(value, {
                        lang,
                        theme: 'tokyo-night'
                    });
                } else {
                    codeHtml = highlighter.codeToHtml(value, {
                        lang: 'text', // 作为纯文本处理
                        theme: 'tokyo-night'
                    });
                }
                const encodedValue = encodeBase64(value);
                // 在生成的 HTML 中添加代码语言标识
                const langLabel = lang ? `<div class="code-lang">${lang}</div>` : '';
                const copyButton = ` 
                <svg class="copy-button-svg" data-clipboard-text="${encodedValue}" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
               <path d="M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z" />
                </span>
               `
                // 在 `codeHtml` 中插入 `langLabel`
                codeHtml = codeHtml.replace('</pre>', `${langLabel} ${copyButton}</pre>`);

                return codeHtml;
            } catch (error) {
                console.error(`Error highlighting code with language '${lang}':`, error);
                return `<pre><code>${md.utils.escapeHtml(value)}</code></pre>`;
            }
        },
    })
};
const handleApiCall = async (apiFunc: Function, payload: any, nextStep?: typeof eamilHandle.value) => {
    const { code, msg } = await apiFunc(payload)
    if (code !== 200) return $toast.error(msg)
    $toast.success(msg)
    if (nextStep) eamilHandle.value = nextStep
}
const encodeBase64 = (text: string): string => {
    return btoa(unescape(encodeURIComponent(text)));
};
const getBlogInfo = async () => {
    await initializeMarkdown()
    blogInfo.value = store.blogInfo
    // if (typeof blogInfo.value.Bio == "string") {
    blogInfo.value.bio = md.render(blogInfo.value.bio)
    // }    
}
const logoutHandler = async () => {
    await logout()
    // store.removeUser()
    localStorage.setItem('token', '')
    $toast.success("已退出登录")
    useRouter().push('/login/')
    logoutDialog.value = false
}
const uploadAvatar = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.onchange = async (e) => {
        const file = (e.target as HTMLInputElement).files?.[0];
        if (!file) return;
        try {
            const res = await uploadAvatarApi(file);
            if (res.code === 200) {
                $toast.success('上传成功')
                await getBlogInfo()
            }
        } catch (error) {
            console.error(error);
        }
    }
    getBlogInfo()
    input.click();
};
const getAccount = async () => {
    const { data } = await getAccountApi()
    editPasswordForm.value.username = data.username
}
const clickEditAccountBtn = async () => {
    await getAccount()
    accountDialog.value = true
}
const clickEditEmialBtn = () => {
    emailDialog.value = true
}
const close = () => {
    accountDialog.value = false
}
const validate = (user: User) => {
    if (user.username == null || user.username == '') {
        $toast.error("账号不能为空")
        return false
    }
    if (user.password == null || user.password == '') {
        $toast.error("密码不能为空")
        return false
    }
    if (user.username.length < 3) {
        $toast.error("账号不能小于两位")
        return false
    }
    if (user.password.length < 6) {
        $toast.error("密码不能小于六位")
        return false
    }
    return true
}
const checkOriginalPwdHandle = async () => {
    if (!originalPassword.value) {
        $toast.warning("请输入原始密码")
        return
    }
    const { code, msg } = await checkPasswordApi(originalPassword.value)
    if (code != 200) {
        $toast.error(msg)
        return
    }
    isOldPassword.value = true
    originalPassword.value = ''
}
const checkOriginalEmailHandle = async () => {
    loading.value = true
    handleApiCall(validateOldEmailApi, editEmailForm.value.oldEmail, "validateOldEmailCode").then(() => {
        loading.value = false
    })
}
const editPasswordHandle = async () => {
    if (!(editPasswordForm.value.password && editPasswordForm.value.confirmPassword)) {
        if (editPasswordForm.value.password !== editPasswordForm.value.confirmPassword) {
            $toast.error("两次密码不一致")
            return
        }
    }
    let temp = {
        username: '',
        password: ''
    }
    temp = { ...editPasswordForm.value }
    const { code, msg } = await editPasswordApi(temp)
    if (code != 200) {
        $toast.warning(msg)
        return
    }
    $toast.success(msg)
    editPasswordForm.value = {
        username: '',
        password: '',
        confirmPassword: ''
    }
    accountDialog.value = false
    isOldPassword.value = false
}
const editPasswordClose = () => {
    isOldPassword.value = false
    accountDialog.value = false
}
const resetSendCode = () => {
    if (eamilHandle.value === 'validateOldEmailCode') {
        handleApiCall(validateOldEmailApi, editEmailForm.value.oldEmail, "validateOldEmailCode")
    }
    if (eamilHandle.value === 'validateNewEmailCode') {
        handleApiCall(validateNewEmailApi, editEmailForm.value.newEmail, "validateNewEmailCode")
        console.log(editEmailForm.value.newEmail);
    }
}
const checkOriginalBackHandle = () => {
    emailDialog.value = false
    editEmailForm.value.oldEmail = ''
}
const backToEmail = () => {
    eamilHandle.value = 'validateOldEmail'
}
const valideCodeBtn = async () => {
    let query = {
        code: codeForm.value,
        toEmail: ''
    }
    if (eamilHandle.value == 'validateOldEmailCode') {
        query.toEmail = editEmailForm.value.oldEmail
        handleApiCall(validateCodeApi, query, "validateNewEmail")
        codeForm.value = ''
        return
    }
    query.toEmail = editEmailForm.value.newEmail
    handleApiCall(validateCodeApi, query, "validateOldEmail")
    await editEmail(editEmailForm.value.newEmail)
    emailDialog.value = false
    codeForm.value = ''
}
watch(eamilHandle, (newVal) => {
    console.log(newVal);
})
const editNewEmailHandle = () => {
    loading.value = true
    handleApiCall(validateNewEmailApi, editEmailForm.value.newEmail, "validateNewEmailCode").then(() => {
        loading.value = false
    })
}
onMounted(() => {
    getBlogInfo()
})
</script>
<template>
    <div class="w-screen">
        <div class="mt-10 flex items-center gap-4">
            <div>
                <div @click="uploadAvatar"
                    class="avatar rounded-[50%] w-28 h-28 flex justify-center items-center cursor-pointer">
                    <span class="changeImg">更换头像</span>

                    <img v-if="blogInfo.avatar"
                        class="avatarImg rounded-[50%] size-full transition-all duration-300 object-cover"
                        :src="useRuntimeConfig().public.baseURL + '/static/avatar/' + blogInfo?.avatar">
                    <div v-else
                        class="avatartext size-full text-2xl rounded-[50%] text-white bg-black flex justify-center items-center">
                        {{ blogInfo?.bloggerName?.charAt(0) }}

                    </div>
                </div>
                <div class="text-slate-400 text-xs mt-2 text-center">
                    {{ blogInfo?.motto }}
                </div>
            </div>
            <div class="flex  text-xl font-semibold">
                {{ blogInfo?.bloggerName }}
            </div>
        </div>
        <div v-html="blogInfo?.bio" class="mt-8 markdown-body">
        </div>

        <div class="flex gap-4 fixed right-10 top-5">
            <v-btn color="#000000" @click="clickEditAccountBtn">
                修改账号密码
            </v-btn>
            <v-btn color="#000000" @click="clickEditEmialBtn">
                更换邮箱
            </v-btn>
            <v-btn color="#000000" @click="logoutHandler">
                退出登录
            </v-btn>
        </div>
        <NuxtLink :to="{ path: '/blogger/editor' }" class="fixed right-20 bottom-5 ">
            <v-btn>
                编辑
            </v-btn>
        </NuxtLink>
        <v-dialog v-model="accountDialog" max-width="600" persistent>
            <v-card prepend-icon="mdi-account" title="修改账号" v-if="!isOldPassword">
                <v-form class="p-3">
                    <v-text-field variant="solo-filled" v-model="editPasswordForm.username" label="账号" required>
                    </v-text-field>
                    <v-text-field variant="solo-filled" type="password" v-model="originalPassword" label="输入原始密码"
                        required>
                    </v-text-field>
                </v-form>
                <v-card-actions>
                    <v-btn text="关闭" @click="close"></v-btn>
                    <v-btn text="确认" variant="tonal" @click="checkOriginalPwdHandle"></v-btn>
                </v-card-actions>
            </v-card>
            <v-card prepend-icon="mdi-account" title="如只修改账号名，无需填写密码相关字段。" v-else>
                <v-form class="p-3">
                    <v-text-field variant="solo-filled" v-model="editPasswordForm.username" label="账号" required>
                    </v-text-field>
                    <v-text-field variant="solo-filled" type="password" v-model="editPasswordForm.password"
                        label="输入新密码" required>
                    </v-text-field>
                    <v-text-field variant="solo-filled" type="password" v-model="editPasswordForm.confirmPassword"
                        label="确认密码" required>
                    </v-text-field>
                </v-form>
                <v-card-actions>
                    <v-btn text="关闭" @click="editPasswordClose"></v-btn>
                    <v-btn text="确认" variant="tonal" @click="editPasswordHandle"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog v-model="emailDialog" max-width="500" persistent>
            <v-card class="p-7" prepend-icon="mdi-account" title="修改邮箱" v-if="eamilHandle == 'validateOldEmail'">
                <v-form class="p-3">
                    <v-text-field variant="solo-filled" v-model="editEmailForm.oldEmail" label="输入原始邮箱" required>
                    </v-text-field>
                </v-form>
                <v-card-actions>
                    <v-row dense>
                        <v-col cols="4">
                            <v-btn text="返回" class="my-2 " size="large" variant="elevated" block
                                @click="checkOriginalBackHandle">
                            </v-btn>
                        </v-col>
                        <v-col cols="8">
                            <v-btn text="下一步" class="my-2 text-white bg-black" size="large" variant="tonal" :loading
                                block @click="checkOriginalEmailHandle">
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-card-actions>

            </v-card>
            <v-form v-if="eamilHandle === 'validateOldEmailCode' || eamilHandle === 'validateNewEmailCode'"
                class="w-[500px]  p-2  flex opacity-95 flex-col rounded-md bg-white  mx-auto mt-32">
                <div class="flex flex-col items-center justify-center gap-2">
                    <span class="text-xl font-semibold ">
                        验证码
                    </span>
                    <span class="text-xs text-gray-500">
                        已向{{ eamilHandle == 'validateOldEmailCode' ? editEmailForm.oldEmail : editEmailForm.newEmail
                        }}发送了验证码
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
                        <v-btn class="my-2 text-white bg-black" size="large" variant="elevated" block
                            @click="valideCodeBtn">
                            验证
                        </v-btn>
                    </v-col>
                </v-row>
            </v-form>
            <v-card prepend-icon="mdi-account" title="新邮箱" v-if="eamilHandle == 'validateNewEmail'">
                <v-form class="p-3">
                    <v-text-field variant="solo-filled" v-model="editEmailForm.newEmail" label="输入新的邮箱" required>
                    </v-text-field>
                </v-form>
                <v-card-actions>
                    <v-row dense>
                        <v-col cols="4">
                            <v-btn text="返回" class="my-2 " size="large" variant="elevated" block @click="backToEmail">
                            </v-btn>
                        </v-col>
                        <v-col cols="8">
                            <v-btn text="下一步" class="my-2 text-white bg-black" :loading size="large" variant="tonal"
                                block @click="editNewEmailHandle">
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<style scoped>
.markdown-body {
    box-sizing: border-box;
    max-width: 920px;
    width: 100%;
    margin-right: 20px;

}

@media (max-width: 767px) {
    .markdown-body {
        padding: 15px;
    }
}

.avatar:hover .avatarImg {
    filter: brightness(0.7)
}

.avatar:hover .avatartext {
    filter: brightness(0.7)
}

.avatar:hover .changeImg {
    opacity: 1;
}

.changeImg {
    font-size: 16px;
    z-index: 999;
    position: absolute;
    color: white;
    font-weight: 600;
    transition: all 0.3s;
    opacity: 0;
}
</style>