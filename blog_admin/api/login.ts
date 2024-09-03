import request from '~/utils/request'
import { type Account} from '~/types/User'
import { type Result } from '~/types/Result'
export function loginApi(account: Account): Promise<Result> {
  return request('/login', {
    method: 'post',
    body: account
  })
}

export function registerApi(account: Account): Promise<Result> {
  return request('/register', {
    method: 'post',
    body: account
  })
}
