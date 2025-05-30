<script setup lang="ts">
import { homePageInfoApi } from '~/api/blog'
import * as echarts from 'echarts';
if (process.client) {
    import('echarts-wordcloud')
}
let categoryVOList = <any>[]
let tagsVOList = <any>[]
const countList = ref(<any>[])
const contactList = ref(<any>[])
const visitsList = ref(<any>[])
const viewTime = ref(<any>[])
const pageViews = ref(<any>[])
const visitors = ref(<any>[])
const userHomePageInfo = async () => {
    try {
        const { data } = await homePageInfoApi()
        categoryVOList = data.categoryVOList.map((item: any) => {
            return {
                value: item.articleCount,
                name: item.categoryName
            }
        })
        tagsVOList = data.tagsVOList.map((item: any) => {
            return {
                value: item.articleCount,
                name: item.tagsName
            }
        })
        countList.value = [
            {
                title: '文章总数',
                count: data.articleCount || 0
            },
            {
                title: '分类总数',
                count: data.categoryCount || 0
            },
            {
                title: '标签总数',
                count: data.tagsCount || 0
            }
        ]
        contactList.value = [
            {
                title: '朋友数量',
                count: data.friendCount || 0
            },
            {
                title: '留言数量',
                count: data.commentCount || 0
            },
            {
                title: '文章总阅读量',
                count: data.articleViewCount || 0
            },
        ]
        visitsList.value = [
            {
                title: '今日浏览量',
                count: data.todayView || 0
            },
            {
                title: '昨日浏览量',
                count: data.yesterdayView || 0
            },
            {
                title: '页面总浏览量',
                count: data.pageViewCount || 0
            },
        ]
        viewTime.value = data.viewsChartList.map((item: any) => new Date(item.viewTime).toLocaleDateString("zh-CN"))
        pageViews.value = data.viewsChartList.map((item: any) => item.pageViews)
        visitors.value = data.viewsChartList.map((item: any) => item.visitors)
    } catch (error) {
        console.error('获取博客信息失败:', error);
    }

}
const generateCategoryChart = () => {
    const categoryDom = document.getElementById('category');
    if (echarts.getInstanceByDom(categoryDom!)) {
        echarts.dispose(categoryDom!);

    }
    const categoryChart = echarts.init(categoryDom, 'dark');

    const categoryOptions = {
        title: {
            text: '文章分类统计',
            left: 'center',
            top: 20
        },
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        visualMap: {
            show: false,
            min: 0,
            max: 10,
            inRange: {
                colorLightness: [0, 1]
            }
        },
        legend: {
            left: 'center',
            top: 'bottom'
        },
        series: [
            {
                name: '分类统计',
                type: 'pie',
                radius: '55%',
                center: ['50%', '50%'],
                data: categoryVOList.sort(function (a: any, b: any) {
                    return a.value - b.value;
                }),
                roseType: 'radius',
                label: {
                    show: true,
                    color: '#ffffff',
                    fontWeight: 600
                },
                labelLine: {
                    smooth: 0.2,
                    length: 10,
                    length2: 20,
                },
                itemStyle: {
                    color: '#0c66ec',
                },

            }
        ]
    };
    // 绘制图表
    categoryChart.setOption(categoryOptions);
}
const generateTagsCloudChart = () => {
    const tagsDom = document.getElementById('tagsCloud')
    if (echarts.getInstanceByDom(tagsDom!)) {
        echarts.dispose(tagsDom!)
    }
    const tagsCloudChart = echarts.init(tagsDom, 'dark')
    const tagsCloudOptions = {
        title: {
            text: '文章标签统计',
            left: 'center',
            top: 20
        },
        series: [{
            type: 'wordCloud',
            // shape这个属性虽然可配置，但是在词的数量不太多的时候，效果不明显，shape也可以是一个函数，比如希望是矩形的时候（来自官方githup问答区）
            shape: function shapeSquare(theta: any) {
                return Math.min(
                    1 / Math.abs(Math.cos(theta)),
                    1 / Math.abs(Math.sin(theta))
                )
            }
            ,
            // 这个功能还没用过
            keepAspect: false,
            // 这个是可以自定义背景图片的，词云会按照图片的形状排布，所以有形状限制的时候，最好用背景图来实现，而且，这个背景图一定要放base64的，不然词云画不出来
            // maskImage: maskImage,
            // 下面就是位置的配置
            left: 'center',
            top: 'center',
            width: '100%',
            height: '80%',
            // 词的大小，最小12px，最大60px，可以在这个范围调整词的大小
            sizeRange: [12, 60],
            // 每个词旋转的角度范围和旋转的步进
            rotationRange: [-90, 90],
            rotationStep: 45,
            // 词间距，数值越小，间距越小，这里间距太小的话，会出现大词把小词套住的情况，比如一个大的口字，中间会有比较大的空隙，这时候他会把一些很小的字放在口字里面，这样的话，鼠标就无法选中里面的那个小字，这里可以用函数根据词云的数量动态返回间距
            gridSize: 20,
            // 允许词太大的时候，超出画布的范围
            drawOutOfBound: false,
            // 布局的时候是否有动画
            layoutAnimation: true,
            // 这是全局的文字样式，相对应的还可以对每个词设置字体样式
            textStyle: {
                fontFamily: 'sans-serif',
                fontWeight: 'bold',
                // 颜色可以用一个函数来返回字符串，这里是随机色
                color: function () {
                    // Random color
                    return 'rgb(' + [
                        Math.round(Math.random() * 127 + 200), // Red component (128-255)
                        Math.round(Math.random() * 127 + 128), // Green component (128-255)
                        Math.round(Math.random() * 127 + 128)  // Blue component (128-255)
                    ].join(',') + ')';
                }
            },
            emphasis: {
                textStyle: {
                    textShadowBlur: 10,
                    textShadowColor: '#333',
                    color: '#ffffff',
                }
            },
            // 数据必须是一个数组，数组是对象，对象必须有name和value属性
            data: tagsVOList
        }]
    }
    tagsCloudChart.setOption(tagsCloudOptions)
}
const generateViewChart = () => {
    const viewDom = document.getElementById('viewChart')
    if (echarts.getInstanceByDom(viewDom!)) {
        echarts.dispose(viewDom!)
    }
    const ViewsChart = echarts.init(viewDom, 'dark')
    const ViewsChartOptions = {
        title: {
            text: '过去七天流量趋势',
            top: 10,
            left: 10
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'none' // 设置为 'none' 以取消指示线
            }
        },
        legend: {
            data: ['浏览量', 'IP数量'],
            top: 20
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: viewTime.value
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: (value: any) => {
                    return Math.floor(value); // 将值向下取整以显示为整数
                }
            },
            minInterval: 1, // 确保 Y 轴显示为整数

        },
        series: [
            {
                name: '浏览量',
                type: 'line',
                data: pageViews.value,
                smooth: true
            },
            {
                name: 'IP数量',
                type: 'line',
                data: visitors.value,
                smooth: true
            }
        ]
    };
    // 绘制图表
    ViewsChart.setOption(ViewsChartOptions);
    // 添加窗口调整事件
    window.addEventListener('resize', () => {
        ViewsChart.resize();
    });
}

onMounted(async () => {
    await userHomePageInfo()
    generateCategoryChart()
    generateTagsCloudChart()
    generateViewChart()

})
</script>
<template>
    <div class="w-full">
        <div class="text-4xl font-semibold mt-8 block mb-6">
            仪表盘
        </div>
        <div class="bg-black rounded-lg w-full h-24 flex  items-center mb-8">
            <div class="w-96 text-center border-r-4 border-solid border-gray-400 py-2 transition-all duration-300
            cursor-pointer hover:text-slate-300 last:border-r-0 font-semibold" v-for="(item, index) in countList"
                :key="index">
                <div>{{ item.title }}</div>
                <span>{{ item.count }}</span>
            </div>
        </div>

        <div class="border-[3px] rounded-lg border-solid border-black py-6 mb-5">
            <div class="flex items-center">
                <div v-for="(item, index) in contactList" :key="index"
                    class="w-96 text-center transition-all duration-300
                border-r-4 border-solid border-gray-700 py-2 last:border-r-0 cursor-pointer font-semibold hover:text-gray-500">
                    <div>{{ item.title }}</div>
                    <span>{{ item.count }}</span>
                </div>
            </div>
        </div>

        <div class=" rounded-lg bg-black  mb-5 py-6">
            <div class="text-2xl font-semibold mb-6 ml-3">
                流量统计
            </div>
            <div class="flex items-center">
                <div v-for="(item, index) in visitsList" :key="index"
                    class="w-96 text-center transition-all duration-300 
                border-r-4 border-solid border-gray-400 py-2 last:border-r-0 cursor-pointer font-semibold hover:text-gray-500">
                    <div>{{ item.title }}</div>
                    <span>{{ item.count }}</span>
                </div>
            </div>
        </div>
        <div class="flex justify-between h-[340px]">
            <div id="category" class="w-[400px] h-[300px]"></div>
            <div id="tagsCloud" class="w-[800px] h-[300px]"></div>
        </div>
        <!-- <div class="flex justify-center m-w-full"> -->
            <div id="viewChart" class="w-full h-[400px] mb-4"></div>
        <!-- </div> -->
  
    </div>
</template>

<style scoped>

</style>