<script setup lang="ts">
import { articleDraftListApi, publishArticleListApi, deleteArticleApi } from '~/api/article'
import { type Article, type ArticleBackRes, type ArticleQuery } from '~/types/Article'
import { type Category } from '~/types/Category'
import { categoryAllListApi } from '~/api/category'

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
        sortable: false
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
const startIndex = ref(1)
const dialog = ref(false)
const articleList = ref()
const showarticleList = ref<Array<ArticleBackRes>>([])
const tipsText = ref('')
let currentItem: number[] = []
let isPublish = false
const { $toast } = useNuxtApp()
const router = useRouter()
const itemsPerPage = ref(10)
const loading = ref(true)
const total = ref(0)
const queryParams = ref<ArticleQuery>({
    current: 1,
    size: 10
})
const keyword = ref()
const categoryNameArr = ref([])
const selectedCategoryName = ref('')
const selectedArticleIds = ref([])
const disabled = ref(true)
let categoryList = <any>[]
let isCategoryListFetched = false;
const getArticleList = async () => {
    const { data } = await articleDraftListApi(queryParams.value)
    if (!data.recordList) {
        showarticleList.value = []
        return
    }
    articleList.value = data.recordList
    total.value = data.total
    // Promise.all()方法等待所有Promise对象都resolve后再进行处理
    showarticleList.value = await Promise.all(data.recordList.map((item: ArticleBackRes) => {
        return {
            id: item.id,
            title: item.title,
            createDate: new Date(item.createDate!).toLocaleDateString('zh-CN'),
            categoryName: item.categoryName,
            updateDate: new Date(item.updateDate!).toLocaleDateString('zh-CN')
        }
    }))
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
const publishBtn = (item: any) => {
    tipsText.value = '确定要发布这篇文章么?'
    isPublish = true
    dialog.value = true
    currentItem = [item.id]
}
const deleteBtn = (item: any) => {
    tipsText.value = '确定要删除这篇文章么？删除后无法找回!'
    isPublish = false
    dialog.value = true
    currentItem = [item.id]
}
const switchHandler = async () => {
    let res;
    if (isPublish) {
        res = await publishArticleListApi(currentItem)
    } else {
        res = await deleteArticleApi(currentItem)
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
    queryParams.value.current = page
    queryParams.value.size = itemsPerPage
    if(process.client){
        getArticleList()
    }
}
const deleteBatchBtn = () => {
    dialog.value = true
    tipsText.value = "确定批量删除这些文章嘛,将无法找回!"
    isPublish = false
}
const publishBatchBtn = () => {
    dialog.value = true
    tipsText.value = "确定批量发布这些文章嘛"
    isPublish = true
}
watch((selectedArticleIds), (val) => {
    if (val.length > 0) {
        disabled.value = false
        currentItem = val
        return
    }
    disabled.value = true
})
</script>
<template>
    <div class="w-[100%]">
        <div class="text-2xl font-semibold mt-10 hover:cursor-pointer mb-4">草稿</div>
        <!-- headers中的key对应items中item的属性 -->
        <v-data-table-server :headers="headers as any" :items="showarticleList" @update:options="loadingItem"
            :items-per-page="itemsPerPage" :items-length="total" v-model="selectedArticleIds" show-select
            item-value="id">
            <!-- 为headers中每个item中的key，用来定制某一列 -->
            <template v-slot:item.serial="{ item, index }">
                <span>{{ startIndex + index }}</span>
            </template>
            <template v-slot:top>
                <div class="flex w-[100%] gap-4 mt-4">
                    <div class="w-40">
                        <v-text-field variant="solo-filled" v-model="keyword" density="compact"
                            label="标题"></v-text-field>
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
                        <v-btn @click="publishBatchBtn" variant="tonal" color="#159799" prepend-icon="mdi-upload"
                            :disabled>
                            批量发布
                        </v-btn>
                    </div>
                    <div class=" hover:cursor-not-allowed">
                        <v-btn  @click="deleteBatchBtn" variant="tonal" color="#d12e1f" prepend-icon="mdi-trash-can"
                            :disabled>
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
                    <v-icon title="发布" size="small" @click="publishBtn(item)">
                        mdi-publish
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
.v-card-title {
    font-weight: 600;
    /* color: #159799; */
}
</style>