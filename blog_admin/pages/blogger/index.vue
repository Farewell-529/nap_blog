<script setup lang="ts">
import { blogInfoStore } from '../../store/blogInfo'
import { uploadAvatarApi } from '~/api/user'
import { logout } from '~/api/user'
// @ts-ignore
import MarkdownIt from 'markdown-it';
// @ts-ignore
import { createHighlighter } from 'shiki'
import { type User } from "~/types/User";
import 'github-markdown-css/github-markdown.css';
import { getAccountApi, editAccountApi } from "~/api/user";
const store = blogInfoStore()
const blogInfo = ref<any>({})
const logoutDialog = ref(false)
const { $toast } = useNuxtApp()
const dialog = ref(false)
const account = ref<User>()
const form = ref<User>({
    username: '',
    password: ''
})
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
const encodeBase64 = (text: string): string => {
    return btoa(unescape(encodeURIComponent(text)));
};
const decodeBase64 = (encodedText: string): string => {
    return decodeURIComponent(escape(atob(encodedText)));
}

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
    account.value = data
    form.value = { ...account.value! }
}
const clickEditBtn = () => {
    if (!account.value) {
        dialog.value = true
        getAccount()
        return
    }
    form.value = { ...account.value! }
    dialog.value = true
}
const close = () => {
    dialog.value = false
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
const confirm = async () => {
    if (!validate(form.value)) {
        return
    }
    const res = await editAccountApi(form.value)
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    getAccount()
    $toast.success(res.msg)
    dialog.value = false
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

        <div class="flex gap-4 fixed right-20 top-5">
            <v-btn color="#000000" @click="clickEditBtn">
                修改密码
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
        <v-dialog v-model="dialog" max-width="600" persistent>
            <v-card prepend-icon="mdi-account" title="账号密码">
                <v-form class="p-3">
                    <v-text-field variant="solo-filled" v-model="form.username" label="账号" required></v-text-field>
                    <v-text-field variant="solo-filled" v-model="form.password" label="密码" required></v-text-field>
                </v-form>
                <v-card-actions>
                    <v-btn text="关闭" @click="close"></v-btn>
                    <v-btn color="primary" text="确认" variant="tonal" @click="confirm"></v-btn>
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