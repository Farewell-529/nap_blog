<script setup lang="ts">
import { saveCommentsApi, updateCommentsApi, deleteCommentsApi, commentsListApi } from '~/api/comments'
import { articleAllListApi } from "~/api/article";
import { type Comments, type CommentsQuery, type CommentsRes } from '~/types/Comments';
import { type Result } from '~/types/Result'
import { formatDateToYYYYMMDD } from "~/utils/dateUtils";
const headers = [
    {
        title: '头像',
        key: 'avatar',
        width: '80px',
        align: 'center',
        sortable: false
    },
    {
        title: '昵称',
        key: 'name',
        maxWidth: '140px',
        nowrap: true,
        align: 'center',
        sortable: false
    },
    {
        title: '内容',
        key: 'content',
        maxWidth: '450px',
        sortable: false
    },
    {
        title: '邮箱',
        key: 'email',
        width: '100px',
        sortable: false
    },
    {
        title: '网址',
        key: 'url',
        width: '100px',
        maxWidth: '220px',
        nowrap: true,
        sortable: false
    },
    {
        title: '回复的文章',
        align: 'center',
        key: 'articleName',
        width: '120px',
        sortable: false
    },
    {
        title: '回复的人',
        align: 'center',
        key: 'replyName',
        width: '100px',
        sortable: false
    },
    {
        title: '评论日期',
        align: 'center',
        key: 'createDate',
        width: '100px',
        sortable: false
    },
    {
        title: '操作',
        width: '50px',
        align: 'center',
        key: 'handler',
        sortable: false
    }
]
const showCommentsList = ref<CommentsRes[]>()
const dialog = ref(false)
const whichOne = ref('')
const dialogTitle = ref('')
const itemsPerPage = ref(10)
const loading = ref(true)
const total = ref(0)
const selectedCommentsIds = ref([])
const disabled = ref(true)
const tipsText = ref('?')
const selectedArticle = ref<any>({
    id: 0,
    title: ''
})
const articlkNameArr = ref<any>([])
const queryParams = ref<CommentsQuery>({
    current: 1,
    size: 10,
    targetType: 'article'
})
const { $toast } = useNuxtApp()
const form = ref<Comments>({
    targetType: 'article',
    targetId: 0,
    name: '',
    email: '',
    url: '',
    content: '',
    pid: -1,
    replytId: -1
})
let currentItem: number[] = []
const dateDialog = ref(false)
const rowDate = ref()
const getCommentsList = async () => {
    const { data } = await commentsListApi(queryParams.value)
    total.value = data.total || 0
    showCommentsList.value = data.recordList?.map((item: CommentsRes) => {
        const date = new Date(item.createDate!)
        return {
            ...item,
            createDate: date.toLocaleDateString('zh-CN')
        }
    })
    loading.value = false
}
const editBtn = (item: any) => {
    whichOne.value = 'edit'
    dialogTitle.value = '修改评论'
    form.value = {
        id: item.id,
        content: item.content,
        name: item.name,
        email: item.email,
        url: item.url,
        pid: item.pid,
        targetId: item.targetId
    }
    selectedArticle.value.title = item.articleName
    dialog.value = true
    getArticleList()
}
const deleteBtn = (item: any) => {
    whichOne.value = 'delete'
    currentItem = [item.id]
    dialog.value = true
    tipsText.value = '确定删除这条评论嘛'
}
const saveBtn = async () => {
    whichOne.value = 'save'
    dialogTitle.value = '添加评论'
    dialog.value = true
    form.value = {
        targetType: 'article'
    }
    getArticleList()
}
const handlerOption = async () => {
    let option = whichOne.value
    let res: Result = { code: 200, msg: '', data: null }
    if (option == 'delete') {
        res = await deleteCommentsApi(currentItem)
        selectedCommentsIds.value = []
    } if (option == 'edit') {
        form.value.targetId = selectedArticle.value.id
        res = await updateCommentsApi(form.value)
    } if (option == 'save') {
        form.value.targetId = selectedArticle.value.id
        res = await saveCommentsApi(form.value)
    }

    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    getCommentsList()
}
const searchHandle = () => {
    getCommentsList()
}
const clearHandle = () => {
    queryParams.value = {
        current: 1,
        size: 10,
        targetType: 'article'
    }
    rowDate.value=null
    getCommentsList()
}
const loadingItem = ({ page, itemsPerPage }: any) => {
    loading.value = true
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    if (process.client) {
        getCommentsList()
    }
}
const deleteBatchBtn = () => {
    dialog.value = true
    whichOne.value = 'delete'
    tipsText.value = '确定删除这些评论嘛'
}
const closeHandle = () => {
    dialog.value = false
    selectedArticle.value = {
        id: 0,
        title: ''
    }
}
const getArticleList = async () => {
    if (articlkNameArr.value.length != 0) return
    const { data } = await articleAllListApi()
    articlkNameArr.value = data
    selectedArticle.value = articlkNameArr.value.find((article: any) => article.title == selectedArticle.value.title)
}
watch((selectedCommentsIds), (val) => {
    if (val.length > 0) {
        disabled.value = false
        currentItem = val
        return
    }
    disabled.value = true
})
const clickDatePicker = () => {
    queryParams.value.createDate = formatDateToYYYYMMDD(new Date(rowDate.value))
    dateDialog.value = false
}
</script>
<template>
    <div class="w-full">
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer ">评论</div>
        <v-data-table-server :headers="headers as any" :items="showCommentsList" @update:options="loadingItem"
            :items-per-page="itemsPerPage" :items-length="total" :show-current-page="false" :loading
            v-model="selectedCommentsIds" show-select item-value="id" width="600">
            <template v-slot:top>
                <div class="flex w-[100%] gap-4 mt-4">
                    <div class="w-40">
                        <v-text-field variant="solo-filled" v-model="queryParams.keyword" density="compact"
                            label="评论内容"></v-text-field>
                    </div>
                    <div class="w-52">
                        <v-menu v-model="dateDialog" :close-on-content-click="false">
                            <template v-slot:activator="{ props }">
                                <v-text-field v-bind="props"  label="选择日期"
                                    v-model="queryParams.createDate" density="compact" readonly variant="solo-filled"
                                    append-inner-icon="mdi-calendar-month-outline" />
                            </template>
                            <v-date-picker  :hide-header="true"
                                v-model="rowDate" @update:modelValue="clickDatePicker">
                            </v-date-picker>
                        </v-menu>
                    </div>
                    <v-btn size="small" icon="mdi-magnify" @click="searchHandle"></v-btn>
                    <v-btn size="small" icon="mdi-refresh" @click="clearHandle"></v-btn>
                    <div class="ml-auto pr-3">
                        <v-btn @click="saveBtn">
                            添加评论
                        </v-btn>
                    </div>
                </div>
                <div class="flex gap-2">
                    <div class=" hover:cursor-not-allowed">
                        <v-btn @click="deleteBatchBtn" variant="tonal" color="#d12e1f" prepend-icon="mdi-trash-can"
                            :disabled>
                            批量删除
                        </v-btn>
                    </div>
                </div>
            </template>
            <template v-slot:item.avatar="{ item }">
                <div class="w-[100%] flex justify-center">
                    <img class="size-11 my-2 rounded-lg"
                        :src="'https://www.gravatar.com/avatar/' + item.avatar + '?d=mysteryman'" alt="">
                </div>
            </template>
            <template v-slot:item.handler="{ item }">
                <div class="flex gap-4 justify-center">
                    <v-icon size="small" @click="editBtn(item)">
                        mdi-pencil
                    </v-icon>
                    <v-icon size="small" @click="deleteBtn(item)">
                        mdi-delete
                    </v-icon>
                </div>
            </template>
        </v-data-table-server>

        <v-dialog v-model="dialog" max-width="400" persistent>
            <v-card title="提示" v-if="whichOne == 'delete'">
                <v-card-text>
                    {{ tipsText }}
                </v-card-text>
                <template v-slot:actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="dialog = false">
                        算了
                    </v-btn>
                    <v-btn @click="handlerOption" color="primary" variant="tonal">
                        确定啊
                    </v-btn>
                </template>
            </v-card>
            <v-card prepend-icon="mdi-account" :title="dialogTitle" v-else width="600px">
                <v-card-text>
                    <v-form>
                        <v-combobox variant="solo-filled" v-model="selectedArticle" item-value="id"
                            :items="articlkNameArr" label="对应文章">
                        </v-combobox>
                        <v-row dense>
                            <v-col cols="4">
                                <v-text-field variant="solo-filled" v-model="form.name" label="昵称" required />
                            </v-col>
                            <v-col cols="4">
                                <v-text-field variant="solo-filled" v-model="form.email" label="邮箱" required />
                            </v-col>
                            <v-col cols="4">
                                <v-text-field variant="solo-filled" v-model="form.url" label="网址" required />
                            </v-col>
                        </v-row>
                        <v-textarea label="内容" variant="solo-filled" v-model="form.content"></v-textarea>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="关闭" @click="closeHandle"></v-btn>
                    <v-btn color="primary" text="保存" variant="tonal" @click="handlerOption"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped></style>