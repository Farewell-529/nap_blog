import request from '~/utils/request'
import { type Friend, type FriendQuery } from '~/types/Friend'
import { type Result } from '~/types/Result'

export function FriendListApi(params:FriendQuery): Promise<Result> {
    return request(`/admin/friend/list`, {
        method: 'get',
        params
    })
}
export function getFriendByIdApi(id:number): Promise<Result> {
    return request(`/admin/friend/${id}`, {
        method: 'get'
    })
}
export function saveFriendApi(firend:Friend): Promise<Result> {
    return request(`/admin/friend`, {
        method: 'post',
        body:firend
    })
}
export function updateFriendApi(firend:Friend): Promise<Result> {
    return request(`/admin/friend`, {
        method: 'put',
        body:firend
    })
}
export function deleteFriendByIdApi(ids:number[]): Promise<Result> {
    return request(`/admin/friend/delete`, {
        method: 'delete',
        body:ids
    })
}