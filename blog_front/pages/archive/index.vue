<script setup lang="ts">
import { archiveListApi } from "~/api/blog";
const showList = ref<{ [year: string]: any[] }>({}) // 分组后的数据
const total = ref(0)
const yearCount = ref([])
const getArchiveList = async () => {
    const { data } = await archiveListApi()
    const groupedData: { [year: string]: any[] } = {}
    total.value = data.total
    data.recordList.forEach((item: any) => {
        const date = new Date(item.createDate)
        const year = date.getFullYear().toString() // 获取年份
        const formattedDate = date.toLocaleDateString('zh-CN')

        if (!groupedData[year]) {
            groupedData[year] = [] // 初始化年份分组
        }

        groupedData[year].push({
            id: item.id,
            articleTitle: item.articleTitle,
            categoryName: item.categoryName,
            createDate: formattedDate,
            timestamp: date.getTime()
        })
    })

    // 对每个年份内的文章按日期降序排列
    for (const year in groupedData) {
        groupedData[year] = groupedData[year].sort((a: any, b: any) => b.timestamp - a.timestamp)
    }

    showList.value = groupedData
}


onMounted(() => {
    getArchiveList()
})
</script>

<template>
    <div class="flex flex-col items-center  mt-15 font-mono">
        <div class="text-2xl font-semibold mb-5">
            目前总共{{ total }}篇文章，再接再厉~
        </div>
        <div class="min-w-[920px]">
            <!-- 对年份进行排序后遍历显示 -->
            <div class="mb-10" v-for="year in Object.keys(showList).sort((a, b) => Number(b) - Number(a))" :key="year">
                <div class="flex font-semibold gap-2">
                    <h2 class="text-xl  mb-2">{{ year }}</h2> <!-- 显示年份 -->
                    <span>({{ showList[year].length }})</span>
                </div>
                <div class="text-sm flex justify-between font-semibold" v-for="item in showList[year]" :key="item.id">
                    <div class="flex gap-3 mb-1 ">
                        <span>{{ item.createDate }}</span>
                        <NuxtLink :to="{ path: `/posts/${item.id}`, query: { id: item.id } }" class="router">{{
                item.articleTitle }}</NuxtLink>
                    </div>
                    <div>{{ item.categoryName }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.router {
    position: relative;
    padding-bottom: 0.1rem;
    font-size: 16px;
    transition: border-bottom 0.2s ease-in-out, transform 0.2s ease-in-out;
}

.router::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 2px;
    background-color: var(--btn-bg-color);
    /* 初始状态下，宽度为 0，元素不可见 */
    transform: scaleX(0);
    transform-origin: bottom right;
    /* 添加过渡效果 */
    transition: transform 0.2s ease-in-out;

}

.router:hover::after {
    /* 悬停时，元素扩展到原始宽度 */
    transform: scaleX(1);
    transform-origin: bottom left;
}
</style>
