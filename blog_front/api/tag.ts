import request from '~/utils/request'
import { type Result } from '~/types/Result'

export function tagsCountsListApi(): Promise<Result> {
    return request(`/api/tags/countsList`, {
        method: 'get'
    })
}