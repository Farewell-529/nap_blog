<script setup lang="ts">
// import MarkdownEditor from '@/components/Markdown-editor.vue'
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import { publishArticleApi, editArticleApi, articleByIdApi, saveArticleApi } from '~/api/article'
import { categoryAllListApi, savecategoryApi } from '~/api/category'
import { uploadImgApi } from '~/api/user'
import { tagsAllListApi } from '~/api/tags'
import { blogInfoStore } from '~/store/blogInfo'
import { type Article, type ArticleInfoRes } from '~/types/Article'
import { type Category } from '~/types/Category'
import { type Tags } from '~/types/Tags'

import { Undo2 } from 'lucide-vue-next'
const store = blogInfoStore()
const route = useRoute()
const articleId = parseInt(route.query.id as string)
const showArticleVO = ref<ArticleInfoRes>({})
const dialog = ref(false)
const categoryList = ref()
const selectedCategoryName = ref()
let selectedCategoryId = 0
let selectedTagsList: Array<Tags>
const categoryArr = ref<string[]>([])
const selectTagsArr = ref<string[]>([])
const tagsArr = ref<string[]>([])
const tagsList = ref()
const titleInput = ref<string>()
let originalTitle = ''
let originalCategory = ''
let originalTags: Array<string> = []
let originalText = ''
const isCallback = ref(false)
const { $toast } = useNuxtApp()
const isAdd = articleId ? false : true
const isSave = ref(false)
const tipsText = ref()
const btnText = ref(isAdd ? '发布' : '修改')
const articleInfo = ref<Article>({
    userId: 1,
})
let editArticle: Article = {
    id: 0,
    title: '',
    content: '',
    categoryId: 0,
    tagsList: []
}
const content = ref();
const toolbarsExclude = [
    'github'
] as any;
const getCategoryList = async () => {
    const { data } = await categoryAllListApi()
    data.forEach((item: Category) => {
        categoryArr.value.push(item.categoryName!)
    })
    categoryList.value = data
}
const getTagsList = async () => {
    const { data } = await tagsAllListApi()
    data.forEach((item: Tags) => {
        tagsArr.value.push(item.tagsName!)
    })
    tagsList.value = data
}

const transformTagsList = () => {
    selectedTagsList = selectTagsArr.value.map(item => {
        return { tagsName: item }
    })
    isAdd ? articleInfo.value.tagsList = selectedTagsList : editArticle.tagsList = selectedTagsList
}

//找到对应的categoryId,如果找不到就添加分类
const findcategoryId = async () => {
    if (!selectedCategoryName.value) return
    const selectedCategory = categoryList.value.find((item: Category) => item.categoryName === selectedCategoryName.value)
    //如果没有这个分类就新增
    if (!selectedCategory) {
        const { data } = await savecategoryApi({ categoryName: selectedCategoryName.value })
        if (isAdd) {
            articleInfo.value.categoryId = data.categoryId
            return
        }
        selectedCategoryId = data.categoryId
        return
    }
    if (isAdd) {
        articleInfo.value.categoryId = selectedCategory.id
        return
    }
    selectedCategoryId = selectedCategory.id
}

const getArticleById = async () => {
    //判断是否为编辑
    if (isAdd) return
    const { data } = await articleByIdApi(articleId)
    showArticleVO.value = data
    content.value = showArticleVO.value?.content as string
    titleInput.value = showArticleVO.value.title
    selectedCategoryName.value = showArticleVO.value.categoryName
    selectTagsArr.value = showArticleVO.value.tags!.map(item => item.tagsName!)
    originalTitle = titleInput.value || ''
    originalText = content.value
    originalCategory = selectedCategoryName.value
    originalTags = selectTagsArr.value
}

const saveBtn = () => {
    tipsText.value = '确定保存到草稿吗'
    isSave.value = true
    dialog.value = true
}
const addArticleHandler = async () => {
    if (checkEmptiy()) return
    //选择分类后的id赋值给articleInfo
    await findcategoryId()
    //将选择后的tags转化为类型为Tags的对象数组，赋值给articleInfo
    transformTagsList()
    articleInfo.value.content = content.value
    articleInfo.value.title = titleInput.value
    let res;
    //判断是发布还是保存
    if (isSave.value) {
        res = await saveArticleApi(articleInfo.value)
    } else {
        res = await publishArticleApi(articleInfo.value)
    }
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    useRouter().push('/articles')
}

const editHandler = async () => {
    if (checkEmptiy()) return
    await findcategoryId()
    //将选择后的tags转化为类型为Tags的对象数组,赋值给editArticle
    transformTagsList()
    editArticle.id = showArticleVO.value.id
    editArticle.title = titleInput.value
    editArticle.content = content.value
    editArticle.categoryId = selectedCategoryId
    const res = await editArticleApi(editArticle)
    if (res.code != 200) {
        $toast.error(res.msg)
        return
    }
    $toast.success(res.msg)
    dialog.value = false
    useRouter().back()
}
const handleBtn = () => {
    tipsText.value = isAdd ? '确定发布这条文章嘛' : '确定修改这条文章嘛'
    dialog.value = true
}
const choseEditOrPublish = () => {
    isAdd ? addArticleHandler() : editHandler()
}
const choseHandle = () => {
    if (isCallback.value) {
        dialog.value = false
        useRouter().back()
        return
    }
    choseEditOrPublish()
}
const callbackFn = () => {
    if (isAdd) {
        useRouter().back()
        return
    }
    if (originalText != content.value || originalCategory != selectedCategoryName.value
        || originalTitle != titleInput.value) {
        dialog.value = true
        isCallback.value = true
        tipsText.value = '好像忘记保存了~确定退出吗?'
        return
    }
    const result = selectTagsArr.value.every((val, index) => val == originalTags[index])
    if (!result) {
        dialog.value = true
        isCallback.value = true
        tipsText.value = '好像忘记保存了~确定退出吗?'
        return
    }
    useRouter().back()
}
const checkEmptiy = () => {
    if (content.value=='' || titleInput.value == '') {
        $toast.error("内容不能为空~")
        dialog.value = false
        return true
    }
}
const onUploadImg = async (files: Array<File>, callback: Function) => {
    files.map(async (file) => {
        await uploadImgApi(file, articleId || 0).then(res => {
            callback([`http://localhost:8080/static/img/${res.data}`])
        })
    })
}
onMounted(() => {
    getArticleById()
    getCategoryList()
    getTagsList()
})
</script>
<template>
    <div class="w-screen h-screen fixed bg-white px-16 z-[999]">
        <div class="mt-2 flex relative ">
            <div class="mr-5 mt-3">
                <v-btn @click="callbackFn">
                    <Undo2 />
                </v-btn>
            </div>
            <div class="w-[300px] mr-10">
                <v-text-field v-model="titleInput" label="文章标题" required hint="设计标题" variant="solo" />
            </div>
            <div v-show="isAdd" class="absolute right-28 top-1">
                <v-btn size="large" @click="saveBtn">
                    保存到草稿
                </v-btn>
            </div>
            <div class="absolute right-4 top-1">
                <v-btn size="large" @click="handleBtn">
                    {{ btnText }}
                </v-btn>
            </div>
            <!-- categorySelect -->
            <div class="w-[250px] mr-10">
                <v-combobox v-model="selectedCategoryName" :items="categoryArr" density="comfortable"
                    label="分类"></v-combobox>
            </div>
            <!-- tagsSelect -->
            <div class="w-[400px]">
                <v-combobox v-model="selectTagsArr" :items="tagsArr" density="comfortable" label="标签"
                    multiple></v-combobox>
            </div>
        </div>
        <div>
            <client-only>
                <md-editor v-model="content" codeTheme="atom" :toolbarsExclude placeholder="请输入...."
                    @onUploadImg="onUploadImg" previewTheme="github">
                </md-editor>
            </client-only>
        </div>
        <v-dialog v-model="dialog" max-width="400" persistent>
            <v-card title="提示">
                <v-card-text>
                    {{ tipsText }}
                </v-card-text>
                <template v-slot:actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="dialog = false">
                        算了
                    </v-btn>
                    <v-btn  text="确认" variant="tonal" @click="choseHandle">
                        确定啊
                    </v-btn>
                </template>
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

.github-theme {
    color: red;
}
</style>