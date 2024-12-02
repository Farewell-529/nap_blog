import { type PageQuery } from '~/types/PageQuery'

export interface Comments{
  id?:number,
  targetType?:string,
  targetId?:number,
  userId?:number,
  pid?:number,
  replytId?:number,
  name?:string,
  content?:string,
  ip?:string,
  status?:number,
  url?:string,
  email?:string,
  isAuthor?:number,
  avatar?:string,
  createDate?:string
}
export interface CommentsRes{
  id?:number,
  targetId?:number,
  userId?:number,
  pid?:number,
  targetName?:string,
  name?:string,
  content?:string,
  ip?:string,
  status?:number,
  url?:string,
  email?:string,
  isAuthor?:number,
  replyName?:string
  avatar?:string,
  articleName?:string,
  createDate?:string
}
export interface CommentsQuery extends PageQuery{
    keyword?:string,
    createDate?:string
    targetType:string
  }