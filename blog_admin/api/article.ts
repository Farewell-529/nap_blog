import request from '~/utils/request'
import { type Article ,type ArticleQuery} from '~/types/Article'
import { type Result } from '~/types/Result'

export function articleListApi(params:ArticleQuery): Promise<Result> {
    return request(`/admin/articles/list`, {
        method: 'get',
        params
    })
}
export function articleAllListApi(): Promise<Result> {
    return request(`/admin/articles/all/list`, {
        method: 'get'
    })
}
export function articleDraftListApi(params:ArticleQuery): Promise<Result> {
    return request(`/admin/articles/draft/list`, {
        method: 'get',
        params
    })
}
export function articleListToDraftApi(ids:number[]): Promise<Result> {
    return request(`/admin/articles/list/draft`, {
        method: 'put',
        body:ids
    })
}
export function articleByIdApi(id:number): Promise<Result> {
    return request(`/admin/articles/${id}`, {
        method: 'get',
    })
}
export function deleteArticleApi(ids:number[]): Promise<Result> {
    return request(`/admin/articles/delete`, {
        method: 'delete',
        body:ids
    })
}
export function saveArticleApi(article:Article): Promise<Result> {
    return request(`/admin/articles/save`, {
        method: 'post',
        body:article
    })
}
export function publishArticleApi(article:Article): Promise<Result> {
    return request(`/admin/articles/publish`, {
        method: 'put',
        body:article
    })
}
export function publishArticleListApi(articleIds:number[]): Promise<Result> {
    return request(`/admin/articles/list/publish`, {
        method: 'put',
        body:articleIds
    })
}
export function editArticleApi(article:Article): Promise<Result> {
    return request(`/admin/articles/update`, {
        method: 'put',
        body:article
    })
}
