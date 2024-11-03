import request from '~/utils/request'
import { type Result } from '~/types/Result'
import { type VisitsQuery,type Visits } from "~/types/Visits";
export function visitisListApi(params:VisitsQuery): Promise<Result> {
    return request(`/admin/visits/list`, {
        method: 'get',
        params
    })
}
export function visitisSaveApi(visits:Visits): Promise<Result> {
    return request(`/admin/visits/save`, {
        method: 'post',
        body:visits
    })
}
export function visitisEditApi(visits:Visits): Promise<Result> {
    return request(`/admin/visits/edit`, {
        method: 'put',
        body:visits
    })
}
export function visitisDeleteApi(ids:number[]): Promise<Result> {
    return request(`/admin/visits/delete`, {
        method: 'delete',
        body:ids
    })
}