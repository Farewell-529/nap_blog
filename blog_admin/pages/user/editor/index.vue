<script setup lang="ts">
import { MdEditor } from 'md-editor-v3';
import { uploadAvatarApi, uploadUserImgApi, getUserInfoApi, editUser } from '~/api/user'
import { Undo2 } from 'lucide-vue-next'
import { type User } from "~/types/User";
const userInfo = ref<User>({
    username: '',
    password: '',
    motto: '',
    bio: '',
    avatar: '',
    githubUrl: '',
    tgUrl: '',
    xUrl: ''
})
const form = ref<User>({
    username: '',
    password: '',
    motto: '',
    bio: '',
    avatar: '',
    githubUrl: '',
    tgUrl: '',
    xUrl: ''
})
const rules = [
    (value: any) => {
        return !value || !value.length || value[0].size < 2000000 || 'Avatar size should be less than 2 MB!'
    },
]
const { $toast } = useNuxtApp()
const dialog = ref(false)
const avatarFile = ref()
const toolbarsExclude = [
    'github'
] as any;
const getUserInfo = async () => {
    const { data } = await getUserInfoApi()
    userInfo.value = data
    form.value = { ...userInfo.value }
    transfromFile()
}
const uploadAvatar = async (file: File) => {
    await uploadAvatarApi(file).then(res => {
        userInfo.value.avatar = res.data
    })
}
const transfromFile = async () => {
    // 如果 avatar 存在且是有效的文件名
    if (userInfo.value.avatar) {
        const avatarUrl = `http://localhost:8080/static/user/avatar/${userInfo.value.avatar}`
        const response = await fetch(avatarUrl)
        const blob = await response.blob()
        const file = new File([blob], userInfo.value.avatar, { type: blob.type })
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
    const { code } = await editUser(form.value)
    if (code !== 200) {
        $toast.error('修改失败')
        return
    }
    $toast.success('修改成功')
    uploadAvatar(avatarFile.value)
}
const editBtn = () => {
    dialog.value = true
}
const close = () => {
    dialog.value = false
    form.value = { ...userInfo.value }
    transfromFile()
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
    getUserInfo()
})
</script>
<template>
    <div class="w-screen h-screen fixed bg-white px-16 z-[999]">
        <div class="flex gap-5  mt-2">
            <v-btn @click="callbackFn">
                <Undo2 />
            </v-btn>
            <div class="w-[280px]">
                <v-text-field v-model="form.username" label="账号" variant="solo" required></v-text-field>
            </div>
            <div class="w-[300px]">
                <v-text-field v-model="form.password" label="密码" variant="solo" required></v-text-field>
            </div>
            <div class="w-[600px]">
                <v-text-field v-model="form.motto" label="格言" required variant="solo"></v-text-field>
            </div>
            <div @click="editBtn">
                <v-btn size="large">修改</v-btn>
            </div>
        </div>
        <client-only>
            <md-editor v-model="form.bio" codeTheme="atom" :toolbarsExclude placeholder="请输入...."
                @onUploadImg="onUploadImg" previewTheme="github">
            </md-editor>
        </client-only>
        <v-dialog v-model="dialog" max-width="600" persistent>
            <v-card prepend-icon="mdi-account" title="个人信息编辑">
                <v-card-text>
                    <v-form>
                        <v-row dense>
                            <v-col cols="12" md="6" sm="6">
                                <v-text-field v-model="form.xUrl" label="X" required></v-text-field>
                            </v-col>
                            <v-col cols="12" md="6" sm="6">
                                <v-text-field v-model="form.githubUrl" label="github" required></v-text-field>
                            </v-col>
                            <v-col cols="12" md="12" sm="6">
                                <v-text-field v-model="form.tgUrl" label="tg"></v-text-field>
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
                    <v-spacer></v-spacer>

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