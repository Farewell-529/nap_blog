import request from '~/utils/request'
import { type Comments, type CommentsQuery } from '~/types/Comments'
import { type Result } from '~/types/Result'
export function commentsListApi(params: CommentsQuery): Promise<Result> {
    return request(`/admin/comments/list`, {
        method: 'get',
        params
    })
}

export function commentsByIdApi(id: number): Promise<Result> {
    return request(`/admin/comments/${id}`, {
        method: 'get',
    })
}
export function deleteCommentsApi(ids: number[]): Promise<Result> {
    return request(`/admin/comments/delete`, {
        method: 'delete',
        body:ids
    })
}
export function saveCommentsApi(comments: Comments): Promise<Result> {
    return request(`/admin/comments/save`, {
        method: 'post',
        body: comments
    })
}
export function updateCommentsApi(comments: Comments): Promise<Result> {
    return request(`/admin/comments/update`, {
        method: 'put',
        body: comments
    })
}