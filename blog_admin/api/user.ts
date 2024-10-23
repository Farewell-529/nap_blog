import request from '~/utils/request'
import { type Result } from '~/types/Result'

export function getBloggerInfoApi(): Promise<Result> {
   return request('/admin/blogger',{
      method:"get"
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