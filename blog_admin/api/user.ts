import request from '~/utils/request'
import { type Result } from '~/types/Result'
import { type User } from "~/types/User";

export function getAccountApi(): Promise<Result> {
   return request('/admin/user', {
      method: 'get'
   })
}
export function editAccountApi(user: User): Promise<Result> {
   return request('/admin/user/edit', {
      method: 'put',
      body: user
   })
}
export function logout(): Promise<Result> {
   return request('/logout', {
      method: 'post'
   })
}

export function uploadAvatarApi(file: File): Promise<Result> {
   const formData = new FormData()
   formData.append('file', file)
   return request('/user/uploadAvatar', {
      method: 'post',
      body: formData
   })
}
export function uploadUserImgApi(file: File): Promise<Result> {
   const formData = new FormData()
   formData.append('file', file)
   return request('/user/uploadImg', {
      method: 'post',
      body: formData
   })
}

export function uploadImgApi(file: File, articleId: Number): Promise<Result> {
   const formData = new FormData()
   formData.append('file', file)
   formData.append('articleId', String(articleId))
   return request('/uploadImg', {
      method: 'post',
      body: formData
   })
}

export function checkPasswordApi(password: string): Promise<Result> {
   return request('/admin/checkPassword', {
      method: 'get',
      params: { password }
   })
}

export function editPasswordApi(account: { username: string, password: string }): Promise<Result> {
   return request('/admin/editPassword', {
      method: 'put',
      body: account
   })
}
export function validateOldEmailApi(email:string): Promise<Result> {
   return request('/admin/validateOldEmail', {
      method: 'get',
      params: {email}
   })
}
export function validateNewEmailApi(email:string): Promise<Result> {
   return request('/admin/validateNewEmail', {
      method: 'get',
      params: {email}
   })
}
export function validateCodeApi(query:{code:string,toEmail:string}): Promise<Result> {
   const {code,toEmail}=query
   return request('/admin/validateCode', {
      method: 'get',
      params: {code,toEmail}
   })
}
export function editEmail(email:string): Promise<Result> {
   return request('/admin/editEmail', {
      method: 'put',
      body: email
   })
}