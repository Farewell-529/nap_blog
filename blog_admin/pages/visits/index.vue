<script setup lang="ts">
import { visitisListApi, visitisSaveApi, visitisEditApi, visitisDeleteApi } from "~/api/visits";
import { type Visits, type VisitsQuery } from "~/types/Visits";
import { formatDateToYYYYMMDD, formatDateToYYYYMMDDhhmmss } from "~/utils/dateUtils";

const headers = [
    {
        title: 'Ip地址',
        key: 'ip',
        align: 'center',
        width: '100px',
        sortable: false
    },
    {
        title: '城市',
        align: 'center',
        width: '100px',
        key: 'city',
        sortable: false
    },
    {
        title: '来自的网站',
        align: 'center',
        width: '100px',
        key: 'fromUrl',
        sortable: false
    },
    {
        title: '浏览器',
        align: 'center',
        width: '100px',
        key: 'browser',
        sortable: false
    },
    {
        title: '浏览时间',
        align: 'center',
        width: '30px',
        key: 'visitTime',
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
const visitsList = ref<Array<Visits>>([])
const queryParams = ref<VisitsQuery>({
    size: 10,
    current: 1
})
const loading = ref(false)
const total = ref(0)
const itemsPerPage = ref(10)
const disabled = ref(true)
const selectedVisitsIds = ref([])
const dialog = ref(false)
const tipsText = ref("")
let currenIds: number[] = []
const dialogTitle = ref("")
const whichOne = ref("")
const form = ref<Visits>({
    city: "",
    fromUrl: "",
    ip: ""
})
const { $toast } = useNuxtApp()
const rowDate = ref()
const dateDialog = ref(false)
const getVisitsList = async () => {
    const { data } = await visitisListApi(queryParams.value)
    visitsList.value = data.recordList.map((item: Visits) => {
        return {
            ...item,
            visitTime: formatDateToYYYYMMDDhhmmss(new Date(item.visitTime!))
        }
    })
    total.value = data.total
    loading.value = false
}
const loadingItem = ({ page, itemsPerPage }: any) => {
    loading.value = true
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    if (process.client) {
        getVisitsList()
    }
}
const deleteBatchBtn = async () => {
    dialog.value = true
    tipsText.value = "确定批量删除这些浏览记录嘛,将无法找回!"
    whichOne.value = "delete"
}
const searchHandle = () => {
    getVisitsList()

}
const clearHandle = () => {
    queryParams.value = {
        size: 10,
        current: 1
    }
    rowDate.value=null
    getVisitsList()
}
const editBtn = (visits: Visits) => {
    whichOne.value = "edit"
    dialog.value = true
    form.value = {
        id: visits.id,
        ip: visits.ip,
        fromUrl: visits.fromUrl,
        city: visits.city,
        browser: visits.browser
    }
}
const saveBtn = (visits: Visits) => {
    whichOne.value = "save"
    dialog.value = true
    form.value = {
        city: "",
        fromUrl: "",
        ip: ""
    }
}
const deleteBtn = (visits: Visits) => {
    dialog.value = true
    tipsText.value = "确定删除这条浏览记录嘛,将无法找回!"
    whichOne.value = "delete"
    currenIds = [visits.id!]
}
const selecteHandle = async () => {
    let res = { code: 200, msg: '', data: null }
    if (whichOne.value == "edit") {
        res = await visitisEditApi(form.value)
    }
    if (whichOne.value == "save") {
        res = await visitisSaveApi(form.value)
    }
    if (whichOne.value == "delete") {
        res = await visitisDeleteApi(currenIds)
        currenIds = []
    }
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    getVisitsList()
}
watch((selectedVisitsIds), (val) => {
    if (val.length > 0) {
        disabled.value = false
        currenIds = selectedVisitsIds.value
        return
    }
    disabled.value = true
})
watch(form, (val) => {
    console.log(val);
})
// const formatDate = (date: Date): string => {
//     const year = date.getFullYear();
//     const month = String(date.getMonth() + 1).padStart(2, '0');
//     const day = String(date.getDate()).padStart(2, '0');
//     return `${year}-${month}-${day}`;
// }
const clickDatePicker = () => {
    queryParams.value.visitTime = formatDateToYYYYMMDD(new Date(rowDate.value));
    dateDialog.value = false
}

</script>
<template>
    <div class="w-full">
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer ">浏览记录</div>
        <!-- headers中的key对应items中item的属性 -->
        <v-data-table-server :headers="headers as any" :items="visitsList" @update:options="loadingItem"
            :items-per-page="itemsPerPage" :items-length="total" :show-current-page="false" :loading show-select
            v-model="selectedVisitsIds" item-value="id">
            <template v-slot:top>
                <div class="flex  gap-4 mt-4">
                    <div class="w-52">
                        <v-text-field variant="solo-filled" v-model="queryParams.ip" density="compact" label="Ip" />
                    </div>
                    <div class="w-36">
                        <v-text-field variant="solo-filled" v-model="queryParams.city" density="compact" label="城市" />
                    </div>
                    <div class="w-56">
                        <v-text-field variant="solo-filled" v-model="queryParams.fromUrl" density="compact"
                            label="来自链接" />
                    </div>
                    <div class="w-52">
                        <v-menu v-model="dateDialog" :close-on-content-click="false">
                            <template v-slot:activator="{ props }">
                                <v-text-field v-bind="props" label="选择日期" v-model="queryParams.visitTime"
                                    density="compact" readonly variant="solo-filled"
                                    append-inner-icon="mdi-calendar-month-outline" />
                            </template>
                            <v-date-picker :hide-header="true" v-model="rowDate" @update:modelValue="clickDatePicker">
                            </v-date-picker>
                        </v-menu>
                    </div>
                    <v-btn size="small" icon="mdi-magnify" @click="searchHandle"></v-btn>
                    <v-btn size="small" icon="mdi-refresh" @click="clearHandle"></v-btn>
                    <div class="ml-auto pr-2">
                        <v-btn @click="saveBtn">
                            添加浏览
                        </v-btn>
                    </div>
                </div>
                <div class="flex gap-2">
                    <div class=" hover:cursor-not-allowed">
                        <v-btn @click="deleteBatchBtn" title="重置" variant="tonal" color="#d12e1f"
                            prepend-icon="mdi-trash-can" :disabled>
                            批量删除
                        </v-btn>
                    </div>
                </div>
            </template>
            <template v-slot:item.handler="{ item }">
                <div class="flex gap-4 justify-center">
                    <v-icon title="编辑" size="small" @click="editBtn(item)">
                        mdi-pencil
                    </v-icon>
                    <v-icon title="删除" size="small" @click="deleteBtn(item)">
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
                    <v-btn  variant="tonal" @click="selecteHandle">
                        确定啊
                    </v-btn>
                </template>
            </v-card>
            <v-card prepend-icon="mdi-account" :title="dialogTitle" v-else>
                <v-card-text>
                    <v-form>
                        <v-text-field v-model="form.ip" label="ip地址" required />
                        <v-text-field v-model="form.city" label="城市" required />
                        <v-text-field v-model="form.browser" label="浏览器" required />
                        <v-text-field v-model="form.fromUrl" label="来自链接" />
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="关闭" @click="dialog = false"></v-btn>
                    <v-btn  text="确认" variant="tonal" @click="selecteHandle"></v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped></style>