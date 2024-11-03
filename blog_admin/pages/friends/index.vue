<script setup lang="ts">
import { saveFriendApi, updateFriendApi, deleteFriendByIdApi, FriendListApi } from '~/api/friend'
import { type Friend, type FriendQuery } from '~/types/Friend';
import { type Result } from '~/types/Result'
import { formatDateToYYYYMMDD } from "~/utils/dateUtils";

const headers = [
    {
        title: '头像',
        key: 'avatarUrl',
        width: '50px',
        align: 'center',
        sortable: false
    },
    {
        title: '名称',
        key: 'friendName',
        width: '50px',
        align: 'center',
        sortable: false
    },
    {
        title: '网站地址',
        align: 'center',
        key: 'url',
        width: '150px',
    },
    {
        title: '网站介绍',
        width: '200px',
        align: 'center',
        key: 'description',
        sortable: false
    },
    {
        title: '创建日期',
        width: '100px',
        align: 'center',
        key: 'createDate',
        sortable: false
    },
    {
        title: '操作',
        width: '100px',
        align: 'center',
        key: 'handler',
        sortable: false
    },
]
const showFriendList = ref<Friend[]>()
const dialog = ref(false)
const whichOne = ref('')
const dialogTitle = ref('')
const itemsPerPage = ref(10)
const loading = ref(true)
const total = ref(0)
const queryParams = ref<FriendQuery>({
    current: 1,
    size: 10
})
const selectedFriendsIds = ref([])
const disabled = ref(true)
let currentItem: number[] = []
const tipsText = ref('')
const { $toast } = useNuxtApp()
const form = ref<Friend>({
    friendName: '',
    url: '',
    avatarUrl: '',
    description: '',
    createDate: ''
})
const dateDialog = ref(false)
const rowDate = ref()
const getFriendList = async () => {
    const { data } = await FriendListApi(queryParams.value)
    total.value = data.total || 0
    showFriendList.value = data.recordList.map((item: Friend) => {
        const date = new Date(item.createDate!)
        return {
            id: item.id,
            avatarUrl: item.avatarUrl,
            url: item.url,
            friendName: item.friendName,
            description: item.description,
            createDate: date.toLocaleDateString('zh-CN')
        }
    })
}
const editBtn = (item: Friend) => {
    whichOne.value = 'edit'
    dialogTitle.value = '修改友链'
    form.value = {
        id: item.id,
        avatarUrl: item.avatarUrl,
        url: item.url,
        friendName: item.friendName,
        description: item.description,
    }
    dialog.value = true
}
const deleteBtn = (item: any) => {
    whichOne.value = 'delete'
    currentItem = [item.id]
    dialog.value = true
    tipsText.value = '确定删除这条友链嘛'
}
const saveBtn = () => {
    whichOne.value = 'save'
    dialogTitle.value = '添加友链'
    dialog.value = true
    form.value = {}
}
const handlerOption = async () => {
    let option = whichOne.value
    let res: Result = { code: 200, msg: '', data: null }
    if (option == 'delete') {
        res = await deleteFriendByIdApi(currentItem)
    } if (option == 'edit') {
        res = await updateFriendApi(form.value)
    } if (option == 'save') {
        res = await saveFriendApi(form.value)
    }

    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    getFriendList()
}
const searchHandle = () => {
    getFriendList()
}
const loadingItem = ({ page, itemsPerPage }: any) => {
    loading.value = true
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    loading.value = false
    if (process.client) {
        getFriendList()
    }
}
const clearHandle = () => {
     queryParams.value={
        current: 1,
        size: 10
     }
     rowDate.value=null
    getFriendList()
}
const closeHandler = () => {
    dialog.value = false
}
const deleteBatchBtn = () => {
    dialog.value = true
    whichOne.value = 'delete'
    tipsText.value = '确定删除这些友链嘛'
}
watch((selectedFriendsIds), (val) => {
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
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer ">朋友圈</div>
        <v-data-table-server :headers="headers as any" :items="showFriendList" @update:options="loadingItem"
            :items-per-page="itemsPerPage" :items-length="total" :show-current-page="false" :loading
            v-model="selectedFriendsIds" show-select item-value="id">
            <template v-slot:top>
                <div class="flex w-[100%] gap-4 mt-4">
                    <div class="w-40">
                        <v-text-field variant="solo-filled" v-model="queryParams.keyword" density="compact"
                            label="朋友名"></v-text-field>
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
                            添加友链
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
            <template v-slot:item.avatarUrl="{ item }">
                <div class="w-[100%] flex justify-center">
                    <img class="size-11 object-contain my-2 rounded-lg" :src="item.avatarUrl" alt="">
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
                    <v-btn  color="primary" variant="tonal" @click="handlerOption">
                        确定啊
                    </v-btn>
                </template>
            </v-card>
            <v-card prepend-icon="mdi-account" :title="dialogTitle" v-else>
                <v-card-text>
                    <v-form>
                        <v-text-field v-model="form.friendName" label="友链名" required density="compact" />
                        <v-text-field v-model="form.url" label="友链地址" required density="compact" />
                        <v-text-field v-model="form.avatarUrl" label="友链头像" required density="compact" />
                        <v-text-field v-model="form.description" label="友链描述" required density="compact" />
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="关闭" @click="closeHandler"></v-btn>
                    <v-btn color="primary" text="确认" variant="tonal" @click="handlerOption"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped></style>