import request from '~/utils/request'
import {  type Tags,type TagsQuery } from '~/types/Tags'
import {  type Result } from '~/types/Result'

export function tagsListApi(params:TagsQuery): Promise<Result> {
    return request('/admin/tags/list', {
        method: 'get',
        params
    })
}

export function tagsAllListApi(): Promise<Result> {
    return request('/tags/list', {
        method: 'get',
    })
}

export function saveTagsApi(tags:Tags): Promise<Result> {
    return request('/admin/tags', {
        method: 'post',
        body:tags
    })
}

export function saveTagsListApi(tagsList:Array<Tags>): Promise<Result> {
    return request('/admin/tags/list', {
        method: 'post',
        body:tagsList
    })
}
export function updateTagsApi(tags:Tags): Promise<Result> {
    return request('/admin/tags', {
        method: 'put',
        body:tags
    })
}
export function deleteTagsApi(ids:number[]): Promise<Result> {
    return request(`/admin/tags/delete`, {
        method: 'delete',
        body:ids
    })
}
export function TagsByIdApi(id:number): Promise<Result> {
    return request(`/admin/tags/${id}`, {
        method: 'get',
    })
}
export function tagsListByArticleIdApi(articleId:number): Promise<Result> {
    return request(`/admin/tags/list/${articleId}`, {
        method: 'get',
    })
}
