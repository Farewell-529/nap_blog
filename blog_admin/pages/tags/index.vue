<script setup lang="ts">
import { saveTagsApi, updateTagsApi, deleteTagsApi, tagsListApi } from '~/api/tags'
import { type Tags, type TagsQuery } from '~/types/Tags';
import { type Result } from '~/types/Result'
import { formatDateToYYYYMMDD } from "~/utils/dateUtils";
const headers = [
    {
        title: '标签名',
        key: 'tagsName',
        width: '50px',
        align: 'center',
        sortable: false
    },
    {
        title: '使用量',
        key: 'useCount',
        width: '50px',
        align: 'center',
        sortable: false
    },
    {
        title: '创建日期',
        align: 'center',
        key: 'createDate',
        width: '100px',
    },
    {
        title: '操作',
        width: '100px',
        align: 'center',
        key: 'handler',
        sortable: false
    }
]
const showTagsList = ref()
const dialog = ref(false)
const whichOne = ref('')
const dialogTitle = ref('')
const itemsPerPage = ref(10)
const loading = ref(false)
const total = ref(0)
const queryParams = ref<TagsQuery>({
    current: 1,
    size: 10
})

const { $toast } = useNuxtApp()
const form = ref<Tags>({
    tagsName: ''
})
const selectedTagsIds = ref([])
const disabled = ref(true)
const tipsText = ref('')
let currentItem: number[] = []
const dateDialog = ref(false)
const rowDate = ref()
const getTagsList = async () => {
    const { data } = await tagsListApi(queryParams.value)
    total.value = data.total || 0
    showTagsList.value = data.recordList.map((item: any) => {
        return {
            id: item.id,
            tagsName: item.tagsName,
            useCount: item.useCount,
            createDate: formatDateToYYYYMMDD(new Date(item.createDate!))
        }
    })
    loading.value = false
}
const editBtn = (item: any) => {
    whichOne.value = 'edit'
    dialogTitle.value = '修改标签'
    form.value = {
        id: item.id,
        tagsName: item.tagsName
    }
    dialog.value = true
}
const deleteBtn = (item: any) => {
    whichOne.value = 'delete'
    currentItem = [item.id]
    dialog.value = true
    tipsText.value = '确定删除这个标签嘛'
}
const saveBtn = () => {
    whichOne.value = 'save'
    dialogTitle.value = '添加标签'
    dialog.value = true
    form.value = {}
}
const handlerOption = async () => {
    let option = whichOne.value
    let res: Result = { code: 200, msg: '', data: null }
    if (option == 'delete') {
        res = await deleteTagsApi(currentItem)
        selectedTagsIds.value = []
    } else if (option == 'edit') {
        res = await updateTagsApi(form.value)
    } else if (option == 'save') {
        res = await saveTagsApi(form.value)
    }
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    getTagsList()
}
const searchHandle = () => {
    getTagsList()
}
const clearHandle = () => {
    queryParams.value = {
        current: 1,
        size: 10
    }
    rowDate.value=null
    getTagsList()
}
const deleteBatchBtn = () => {
    dialog.value = true
    whichOne.value = 'delete'
    tipsText.value = '确定删除这些标签嘛'
}
const loadingItem = ({ page, itemsPerPage }: any) => {
    loading.value = true
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    if (process.client) {
        getTagsList()
    }
}
watch((selectedTagsIds), (val) => {
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
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer ">标签</div>
        <v-data-table-server :headers="headers as any" :items="showTagsList" @update:options="loadingItem"
            :items-per-page="itemsPerPage" :items-length="total" :loading v-model="selectedTagsIds" show-select
            item-value="id">
            <template v-slot:top>
                <div class="flex w-[100%] gap-4 mt-4">
                    <div class="w-40">
                        <v-text-field variant="solo-filled" v-model="queryParams.keyword" density="compact"
                            label="标签名"></v-text-field>
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
                    <div class="ml-auto pr-2">
                        <v-btn @click="saveBtn">
                            添加标签
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
                        <v-text-field v-model="form.tagsName" label="标签名" required />
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="关闭" @click="dialog = false"></v-btn>
                    <v-btn  text="确认" variant="tonal" @click="handlerOption"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped></style>