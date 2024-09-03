import request from '~/utils/request'
import { type Result } from '~/types/Result'

export function categoryCountsListApi(): Promise<Result> {
    return request(`/api/category/countsList`, {
        method: 'get'
    })
}