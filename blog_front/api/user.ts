import request from '~/utils/request'
import { type Result } from '~/types/Result'
// export function userBioApi(): Promise<Result> {
//     return request('/api/user/bio', {
//         method: 'get',
//     })
// }
export function userInfoApi(): Promise<Result> {
    return request('/api/user', {
        method: 'get',
    })
}