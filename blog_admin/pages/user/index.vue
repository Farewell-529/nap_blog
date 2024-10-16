<script setup lang="ts">
import { userStore } from '../../store/userInfo'
import { getUserInfoApi, uploadAvatarApi } from '~/api/user'
import { logout } from '~/api/user'
import { type User } from '~/types/User'
// @ts-ignore
import MarkdownIt from 'markdown-it';
// @ts-ignore
import { createHighlighter } from 'shiki'
import 'github-markdown-css/github-markdown.css';
const store = userStore()
const userInfo = ref<User>()
const logoutDialog = ref(false)
const { $toast } = useNuxtApp()
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

const getUserInfo = async () => {
    await initializeMarkdown()
    const res = await getUserInfoApi()
    if (res.code !== 200) {
        console.log('token失效');
    }
    store.setUser(res.data)
    userInfo.value = store.user
    userInfo.value!.bio = md.render(userInfo.value?.bio)
}
const logoutHandler = async () => {
    await logout()
    store.removeUser()
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
                await getUserInfo()
            }
        } catch (error) {
            console.error(error);
        }
    }
    getUserInfo()
    input.click();
};

onMounted(() => {
    getUserInfo()
})
</script>
<template>
    <div class="w-screen">
        <div class="mt-10 flex items-center gap-4">
            <div>
                <div @click="uploadAvatar"
                    class="avatar rounded-[50%] w-28 h-28 flex justify-center items-center cursor-pointer">
                    <span class="changeImg">更换图片</span>

                    <img v-if="userInfo?.avatar"
                        class="avatarImg rounded-[50%] w-[100%] h-[100%] transition-all duration-300 object-cover"
                        :src="useRuntimeConfig().public.baseURL + '/static/user/avatar/' + userInfo?.avatar" >
                    <div v-else
                        class="avatartext w-[100%] h-[100%] text-2xl rounded-[50%] text-white bg-black flex justify-center items-center">
                        {{ userInfo?.username.charAt(0) }}

                    </div>
                </div>
                <div class="text-slate-400 text-xs mt-2 text-center">
                    {{ userInfo?.motto }}
                </div>
            </div>
            <div class="flex  text-xl font-semibold">
                {{ userInfo?.username }}
            </div>
        </div>
        <div v-html="userInfo?.bio" class="mt-8 markdown-body">
        </div>

        <div class="fixed right-20 top-5">
            <v-btn color="#000000" @click="logoutHandler">
                退出登录
            </v-btn>
        </div>

        <NuxtLink :to="{ path: '/user/editor' }" class="fixed right-20 bottom-5 ">
            <v-btn>
                编辑
            </v-btn>
        </NuxtLink>
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