import { type BloggerInfo } from "~/types/BlogInfo";
export const blogInfoStore = defineStore('blogInfo', () => {
    const blogInfo = ref()
    const setBlogInfo = (newBlogInfo: BloggerInfo) => {
        blogInfo.value = newBlogInfo
    }
    const removeBlogInfo = () => {
        blogInfo.value = {}
    }
    return {
        blogInfo,
        setBlogInfo,
        removeBlogInfo
    }
},
    {
        persist: true
    }
)
