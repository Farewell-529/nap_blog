<script setup lang="ts">
import { articleListApi, deleteArticleApi, articleListToDraftApi } from '~/api/article'
import { categoryAllListApi } from '~/api/category'
import { type Article, type ArticleBackRes, type ArticleQuery } from '~/types/Article'
import { type Category } from '~/types/Category'
// const userId = ref(userStore().user?.id)
const headers = [
    {
        title: '文章标题',
        key: 'title',
        align: 'center',
        width: '100px',
        sortable: false
    },
    {
        title: '分类',
        align: 'center',
        width: '100px',
        key: 'categoryName',
        sortable: false
    },
    {
        title: '创建日期',
        align: 'center',
        width: '100px',
        key: 'createDate',
        sortable: true
    },
    {
        title: '最后更新',
        align: 'center',
        width: '30px',
        key: 'updateDate',
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
const dialog = ref(false)
const showArticleVoList = ref<Array<ArticleBackRes>>([])
let currentItem: number[] = []
const { $toast } = useNuxtApp()
const router = useRouter()
const itemsPerPage = ref(10)
const loading = ref(false)
const search = ref('')
const total = ref(0)
const queryParams = ref<ArticleQuery>({
    current: 1,
    size: 10,
})
const keyword = ref()
const categoryNameArr = ref([])
const selectedCategoryName = ref('')
const tipsText = ref('')
let isDelete = false
const selectedArticleIds = ref()
const disabled = ref(true)
let categoryList = <any>[]
let isCategoryListFetched = false;

const getArticleList = async () => {
    const { data } = await articleListApi(queryParams.value)
    showArticleVoList.value = data.recordList || []
    total.value = data.total || 0
    // Promise.all()等待所有Promise对象都resolve后再进行处理
    showArticleVoList.value = await Promise.all(showArticleVoList.value.map((item: ArticleBackRes) => {
        return {
            id: item.id,
            title: item.title,
            createDate: new Date(item.createDate!).toLocaleDateString('zh-CN'),
            categoryName: item.categoryName,
            updateDate: new Date(item.updateDate!).toLocaleDateString('zh-CN')
        }
    }).sort((a: any, b: any) => new Date(b.createDate).getTime() - new Date(a.createDate).getTime()))
    loading.value = false

}

const editBtn = (item: any) => {
    router.push({
        path: `/write`,
        query: {
            id: item.id
        }
    })
}
const saveBtn = (item: any) => {
    tipsText.value = '确定将这篇文章保存为草稿嘛'
    dialog.value = true
    currentItem = item.id
}
const deleteBtn = (item: any) => {
    tipsText.value = '确定要删除这篇文章么？删除后无法找回!'
    isDelete = true
    dialog.value = true
    currentItem = [item.id]
}
const switchHandler = async () => {
    let res;
    if (isDelete) {
        res = await deleteArticleApi(currentItem)
        selectedArticleIds.value = []
    } else {
        res = await articleListToDraftApi(currentItem)
    }
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    await getArticleList()
    dialog.value = false
    selectedArticleIds.value = []
}
const findCategoryId = () => {
    if (!selectedCategoryName.value) {
        delete queryParams.value.categoryId
    }
    const categoryId = categoryList.find((item: Category) => item.categoryName === selectedCategoryName.value)
    if (!categoryId) {
        return
    }
    queryParams.value.categoryId = categoryId.id
}
const getCategoryList = async () => {
    if (isCategoryListFetched) return
    const { data } = await categoryAllListApi()
    categoryList = data
    categoryNameArr.value = data.map((item: Category) => item.categoryName);
    isCategoryListFetched = true;
}
const searchHandle = () => {
    queryParams.value.keyword = keyword.value
    findCategoryId()
    getArticleList()
}
const clearHandle = () => {
    keyword.value = ''
    selectedCategoryName.value = ''
    delete queryParams.value.keyword
    delete queryParams.value.categoryId
    getArticleList()
}
const loadingItem = ({ page, itemsPerPage }: any) => {
    loading.value = true
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    if (process.client) {
        getArticleList()
    }
}
const deleteBatchBtn = () => {
    dialog.value = true
    tipsText.value = "确定批量删除这些文章嘛,将无法找回!"
    isDelete = true
}
const draftBatchBtn = () => {
    dialog.value = true
    tipsText.value = "确定将这些文章保存到草稿嘛"
    isDelete = false
}
watch((selectedArticleIds), (val) => {
    if (val.length > 0) {
        disabled.value = false
        currentItem = val
        return
    }
    disabled.value = true
})
onMounted(() => {
    getArticleList()
})
</script>
<template>
    <div class="w-full">
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer ">文章</div>
        <!-- headers中的key对应items中item的属性 -->
        <v-data-table-server :headers="headers as any" :items="showArticleVoList" @update:options="loadingItem" :search
            :items-per-page="itemsPerPage" :items-length="total" :show-current-page="false" :loading show-select
            v-model="selectedArticleIds" item-value="id">
            <template v-slot:top>
                <div class="flex w-[100%] gap-4 mt-4">
                    <div class="w-40">
                        <v-text-field variant="solo-filled" v-model="keyword" density="compact" label="标题" />
                    </div>
                    <div class="w-60" @click="getCategoryList">
                        <v-combobox variant="solo-filled" density="compact" v-model="selectedCategoryName"
                            :items="categoryNameArr" label="分类"></v-combobox>
                    </div>
                    <v-btn size="small" icon="mdi-magnify" @click="searchHandle"></v-btn>
                    <v-btn size="small" icon="mdi-refresh" @click="clearHandle"></v-btn>
                    <NuxtLink to="/write" class="ml-auto pr-2">
                        <v-btn>
                            写文
                        </v-btn>
                    </NuxtLink>
                </div>
                <div class="flex gap-2">
                    <div class=" hover:cursor-not-allowed">
                        <v-btn @click="draftBatchBtn" variant="tonal" color="#159799" prepend-icon="mdi-download"
                            :disabled>
                            批量保存
                        </v-btn>
                    </div>
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
                    <v-icon title="保存为草稿" size="small" @click="saveBtn(item)">
                        mdi-download
                    </v-icon>
                    <v-icon title="删除" size="small" @click="deleteBtn(item)">
                        mdi-delete
                    </v-icon>
                </div>
            </template>
        </v-data-table-server>

        <v-dialog v-model="dialog" max-width="400" persistent>
            <v-card title="提示">
                <v-card-text>
                    {{ tipsText }}
                </v-card-text>
                <template v-slot:actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="switchHandler">
                        确定啊
                    </v-btn>
                    <v-btn @click="dialog = false">
                        算了
                    </v-btn>
                </template>
            </v-card>
        </v-dialog>
    </div>
</template>
<style>
th {
    font-weight: 600 !important;
}

.v-btn--icon.v-btn--density-default {
    border-radius: 20% !important;
    /* color: #d12e1f */
}
</style>