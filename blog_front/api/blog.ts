import request from '~/utils/request'
import { type Result } from '~/types/Result'

export function archiveListApi(): Promise<Result> {
    return request(`/api/archive`, {
        method: 'get'
    })
}