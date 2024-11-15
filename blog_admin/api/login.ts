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

export function validateEmailApi(forgetPasswordQuery:{username:string,email:string}): Promise<Result> {
  return request('/validateEmail', {
     method: 'get',
     params:forgetPasswordQuery
  })
}
export function resetPasswordApi(newPassword:string): Promise<Result> {
  return request('/resetPassword', {
     method: 'put',
     body:newPassword
  })
}

export function verifyCodeApi(code:string): Promise<Result> {
  return request('/validateCode', {
     method: 'get',
     params:{code}
  })
}

