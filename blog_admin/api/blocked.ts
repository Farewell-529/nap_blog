import request from '~/utils/request'
import { type Result } from '~/types/Result'
import { type Blocked,type BlockedQuery } from "~/types/Blocked";

export function blockedListApi(params:BlockedQuery): Promise<Result> {
    return request(`/admin/blocked/list`, {
        method: 'get',
        params
    })
}
export function saveBlockedApi(blocked:Blocked): Promise<Result> {
    return request(`/admin/blocked/save`, {
        method: 'post',
        body:blocked
    })
}
export function editBlockedApi(blocked:Blocked): Promise<Result> {
    return request(`/admin/blocked/edit`, {
        method: 'put',
        body:blocked
    })
}
export function deleteBlockedApi(ids:number[]): Promise<Result> {
    return request(`/admin/blocked/delete`, {
        method: 'delete',
        body:ids
    })
}