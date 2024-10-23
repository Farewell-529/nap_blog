<script setup lang="ts">
import { MdEditor } from 'md-editor-v3';
import { uploadAvatarApi, uploadUserImgApi } from '~/api/user'
import { getBloggerInfoApi, editBloggerInfoApi } from "~/api/blog";
import { type BloggerInfo, type BlogInfo } from "~/types/BlogInfo";
import { Undo2 } from 'lucide-vue-next'

const bloggerInfo = ref<BloggerInfo>({
    bloggerName: "",
    motto: '',
    bio: '',
    avatar: '',
    github_url: '',
    x_url: '',
    bilibili_url: ''
})
const form = ref<BloggerInfo>({
    bloggerName: "",
    motto: '',
    bio: '',
    avatar: '',
    github_url: '',
    x_url: '',
    bilibili_url: ''

})
const editFormArr = ref<Array<BlogInfo>>([])
const rules = [
    (value: any) => {
        return !value || !value.length || value[0].size < 2000000 || '头像大小应该小于 2 MB!'
    },
]
const { $toast } = useNuxtApp()
const dialog = ref(false)
const avatarFile = ref()
const toolbarsExclude = [
    'github'
] as any;
const getBlogInfo = async () => {
    const { data } = await getBloggerInfoApi()
    bloggerInfo.value=data
  
    form.value = { ...bloggerInfo.value }
    transfromFile()
}
const uploadAvatar = async (file: File) => {
    await uploadAvatarApi(file).then(res => {
        bloggerInfo.value.avatar = res.data
    })
}
const transfromFile = async () => {
    // 如果 avatar 存在且是有效的文件名
    if (bloggerInfo.value.avatar) {
        const avatarUrl = `http://localhost:8080/static/avatar/${bloggerInfo.value.avatar}`
        const response = await fetch(avatarUrl)
        const blob = await response.blob()
        const file = new File([blob], bloggerInfo.value.avatar, { type: blob.type })
        avatarFile.value = file
    }
}
const onUploadImg = async (files: Array<File>, callback: Function) => {
    files.map(async (file) => {
        await uploadUserImgApi(file).then(res => {
            callback([`http://localhost:8080/static/img/${res.data}`])
        })
    })
}
const editUserHandle = async () => {
    // editFormArr.value = Object.entries(form.value).map(([key, value]) => {
    //     return {
    //         infoKey: key,
    //         value: value
    //     }
    // })
    const { code } = await editBloggerInfoApi(form.value)
    if (code !== 200) {
        $toast.error('修改失败')
        return
    }
    if (form.value.avatar != avatarFile.value.name) {
        uploadAvatar(avatarFile.value)
    }
    $toast.success('修改成功')
}
const editBtn = () => {
    dialog.value = true
}
const close = () => {
    dialog.value = false
    form.value = { ...bloggerInfo.value }
    // transfromFile()
}
const save = () => {
    editUserHandle()
    dialog.value = false
    callbackFn()
}
const callbackFn = () => {
    useRouter().back()
}
onMounted(() => {
    getBlogInfo()
})
</script>
<template>
    <div class="w-screen h-screen fixed bg-white px-16 z-[999]">
        <div class="flex gap-5  mt-2">
            <v-btn @click="callbackFn">
                <Undo2 />
            </v-btn>
            <!-- <div class="w-[280px]">
                <v-text-field v-model="form.username" label="账号" variant="solo" required></v-text-field>
            </div>
            <div class="w-[300px]">
                <v-text-field v-model="form.password" label="密码" variant="solo" required></v-text-field>
            </div> -->
            <div class="w-[600px]">
                <v-text-field v-model="form.motto" label="格言" required variant="solo"></v-text-field>
            </div>
            <div @click="editBtn">
                <v-btn size="large">修改</v-btn>
            </div>
        </div>
        <div>
            <client-only>
                <md-editor v-model="form.bio" codeTheme="atom" :toolbarsExclude placeholder="请输入...."
                    @onUploadImg="onUploadImg" previewTheme="github">
                </md-editor>
            </client-only>
        </div>
        <v-dialog v-model="dialog" max-width="600" persistent>
            <v-card prepend-icon="mdi-account" title="个人信息编辑">
                <v-card-text>
                    <v-form>
                        <v-row dense>
                            <v-col cols="12" md="6" sm="6">
                                <v-text-field v-model="form.x_url" label="X" required></v-text-field>
                            </v-col>
                            <v-col cols="12" md="6" sm="6">
                                <v-text-field v-model="form.github_url" label="github" required></v-text-field>
                            </v-col>
                            <v-col cols="12" md="12" sm="6">
                                <v-text-field v-model="form.bilibili_url" label="哔哩哔哩" required></v-text-field>
                            </v-col>
                            <v-col cols="12" md="12" sm="6">
                                <v-file-input :rules="rules" v-model="avatarFile"
                                    accept="image/png, image/jpeg, image/bmp" label="Avatar"
                                    placeholder="Pick an avatar" prepend-icon="mdi-camera"
                                    variant="solo-filled"></v-file-input>
                            </v-col>
                        </v-row>
                    </v-form>
                </v-card-text>

                <v-card-actions>
                    <v-btn text="Close" variant="plain" @click="close"></v-btn>
                    <v-btn color="primary" text="Save" variant="tonal" @click="save"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped>
div.default-theme img {
    border: none
}

.md-editor-preview ul {
    list-style: initial;
}

.md-editor-preview ol {
    list-style-type: decimal;
}

.md-editor {
    height: calc(100vh - 100px);
}
</style>