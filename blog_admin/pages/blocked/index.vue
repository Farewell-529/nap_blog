<script setup lang="ts">
import { saveBlockedApi, editBlockedApi, deleteBlockedApi, blockedListApi } from '~/api/blocked'
import { type Blocked, type BlockedQuery } from '~/types/Blocked';
import { type Result } from '~/types/Result'
import { formatDateToYYYYMMDD } from "~/utils/dateUtils";

const headers = [
    {
        title: 'ip地址',
        key: 'ip',
        width: '50px',
        align: 'center',
        sortable: false
    },
    {
        title: '封禁理由',
        key: 'blockReason',
        width: '50px',
        align: 'center',
        sortable: false
    },
    {
        title: '是否封禁',
        align: 'center',
        key: 'status',
        width: '150px',
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
const showBlockedList = ref<Blocked[]>()
const dialog = ref(false)
const whichOne = ref('')
const dialogTitle = ref('')
const itemsPerPage = ref(10)
const loading = ref(true)
const total = ref(0)
const queryParams = ref<BlockedQuery>({
    current: 1,
    size: 10
})
const selectedBlockedIds = ref([])
const disabled = ref(true)
let currentItem: number[] = []
const tipsText = ref('')
const { $toast } = useNuxtApp()
const form = ref<Blocked>({
    ip: '',
    blockReason: '',
    status:1,
    createDate: ''
})
const dateDialog = ref(false)
const rowDate = ref()
const getBlockedList = async () => {
    const { data } = await blockedListApi(queryParams.value)
    total.value = data.total || 0
    showBlockedList.value = data.recordList.map((item: Blocked) => {
        return {
            ...item,
            createDate: formatDateToYYYYMMDD(new Date(item.createDate!))
        }
    })
}
const editBtn = (item: Blocked) => {
    whichOne.value = 'edit'
    dialogTitle.value = '修改黑名单'
    form.value = {
        id: item.id,
        ip: item.ip,
        blockReason: item.blockReason,
        status: item.status,
    }
    dialog.value = true
}
const deleteBtn = (item: any) => {
    whichOne.value = 'delete'
    currentItem = [item.id]
    dialog.value = true
    tipsText.value = '确定删除这条黑名单嘛'
}
const saveBtn = () => {
    whichOne.value = 'save'
    dialogTitle.value = '添加黑名单'
    dialog.value = true
    form.value = {
        ip: '',
        blockReason: '',
        status:1,
    }
}
const handlerOption = async () => {
    let option = whichOne.value
    let res: Result = { code: 200, msg: '', data: null }
    if (option == 'delete') {
        res = await deleteBlockedApi(currentItem)
    } if (option == 'edit') {
        res = await editBlockedApi(form.value)
    } if (option == 'save') {
        res = await saveBlockedApi(form.value)
    }

    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    getBlockedList()
}
const searchHandle = () => {
    getBlockedList()
}
const loadingItem = ({ page, itemsPerPage }: any) => {
    loading.value = true
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    loading.value = false
    if (process.client) {
        getBlockedList()
    }
}
const clearHandle = () => {
    queryParams.value = {
        current: 1,
        size: 10
    }
    rowDate.value = null
    getBlockedList()
}
const closeHandler = () => {
    dialog.value = false
}
const deleteBatchBtn = () => {
    dialog.value = true
    whichOne.value = 'delete'
    tipsText.value = '确定删除这些黑名单嘛'
}
watch((selectedBlockedIds), (val) => {
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
const clickSwitch = async (item: Blocked) => {
    item.status = item.status === 0 ? 1 : (item.status === 1 ? 0 : item.status);
    const { code } = await editBlockedApi(item);
    // 根据返回码显示提示
    if (code === 200) {
        $toast.success("操作成功");
    } else {
        $toast.error("操作失败");
    }
};
const clicFormkSwitch =  () => {
    form.value.status = form.value.status === 0 ? 1 : (form.value.status === 1 ? 0 : form.value.status);
};
</script>
<template>
    <div class="w-full">
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer ">黑名单</div>
        <v-data-table-server :headers="headers as any" :items="showBlockedList" @update:options="loadingItem"
            :items-per-page="itemsPerPage" :items-length="total" :show-current-page="false" :loading
            v-model="selectedBlockedIds" show-select item-value="id">
            <template v-slot:top>
                <div class="flex w-[100%] gap-4 mt-4">
                    <div class="w-40">
                        <v-text-field variant="solo-filled" v-model="queryParams.keyword" density="compact"
                            label="ip地址"></v-text-field>
                    </div>
                    <div class="w-52">
                        <v-menu v-model="dateDialog" :close-on-content-click="false">
                            <template v-slot:activator="{ props }">
                                <v-text-field v-bind="props" label="选择日期" v-model="queryParams.createDate"
                                    density="compact" readonly variant="solo-filled"
                                    append-inner-icon="mdi-calendar-month-outline" />
                            </template>
                            <v-date-picker :hide-header="true" v-model="rowDate" @update:modelValue="clickDatePicker">
                            </v-date-picker>
                        </v-menu>
                    </div>
                    <v-btn size="small" icon="mdi-magnify" @click="searchHandle"></v-btn>
                    <v-btn size="small" icon="mdi-refresh" @click="clearHandle"></v-btn>
                    <div class="ml-auto pr-3">
                        <v-btn @click="saveBtn">
                            添加黑名单
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
            <template v-slot:item.status="{ item }">
                <div class="w-[100%] flex justify-center">
                    <v-switch :model-value="item.status == 1" color="indigo" hide-details
                        @update:modelValue="clickSwitch(item)"></v-switch>
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
                    <v-btn variant="tonal" @click="handlerOption">
                        确定啊
                    </v-btn>
                </template>
            </v-card>
            <v-card prepend-icon="mdi-account" :title="dialogTitle" v-else>
                <v-card-text>
                    <v-form>
                        <v-text-field v-model="form.ip" label="ip地址" required density="compact" />
                        <v-text-field v-model="form.blockReason" label="封禁理由" required density="compact" />
                       <span class="font-semibold">是否封禁</span>
                        <v-switch :model-value="form.status == 1" color="indigo" hide-details
                        @update:modelValue="clicFormkSwitch()"></v-switch>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="关闭" @click="closeHandler"></v-btn>
                    <v-btn text="确认" variant="tonal" @click="handlerOption"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped></style>