import request from '~/utils/request'
import { type Result } from '~/types/Result'

export function friendListApi(): Promise<Result> {
    return request(`/api/friend/list`, {
        method: 'get',
    })
}