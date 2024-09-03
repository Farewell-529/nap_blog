import request from '~/utils/request'
import { type Result } from '~/types/Result'
export function blogInfoApi(): Promise<Result> {
    return request('/admin', {
        method: 'get',
    }
    )
}