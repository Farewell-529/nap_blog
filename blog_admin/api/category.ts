import request from '~/utils/request'
import { type Category, type CategoryQuery } from '~/types/Category'
import { type Result } from '~/types/Result'
export function categoryListApi(params: CategoryQuery): Promise<Result> {
    return request(`/admin/category/list`, {
        method: 'get',
        params
    })
}
export function categoryAllListApi(): Promise<Result> {
    return request(`/category/list`, {
        method: 'get',
    })
}
export function categoryByIdApi(id: number): Promise<Result> {
    return request(`/admin/category/${id}`, {
        method: 'get',
    })
}
export function deletecategoryApi(ids: number[]): Promise<Result> {
    return request(`/admin/category/delete`, {
        method: 'delete',
        body:ids
    })
}
export function savecategoryApi(category: Category): Promise<Result> {
    return request(`/admin/category`, {
        method: 'post',
        body: category
    })
}
export function updatecategoryApi(category: Category): Promise<Result> {
    return request(`/admin/category`, {
        method: 'put',
        body: category
    })
}