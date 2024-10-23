import request from '~/utils/request'
import { type Result } from '~/types/Result'
import { type BloggerInfo, type BlogInfo } from "~/types/BlogInfo";
export function homePageInfoApi(): Promise<Result> {
    return request('/admin', {
        method: 'get',
    }
    )
}
export function getBloggerInfoApi(): Promise<Result> {
    return request('/admin/blogger',{
       method:"get"
    })
 }
 export function editBloggerInfoApi(blogInfoList:BloggerInfo): Promise<Result> {
    return request('/admin/blogger/edit',{
       method:"put",
       body:blogInfoList
    })
 }