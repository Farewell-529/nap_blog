import request from '~/utils/request'
import { type Result } from '~/types/Result'

export function commentstListApi(articleId: number): Promise<Result> {
    return request(`/api/comments/list`, {
        method: 'get',
        params: { articleId }
    })
}

export function saveCommentstApi(comments: any): Promise<Result> {
    return request(`/api/comments/save`, {
        method: 'post',
        body: comments
    })
}