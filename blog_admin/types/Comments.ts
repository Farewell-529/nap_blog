import { type PageQuery } from '~/types/PageQuery'

export interface Comments{
  id?:number,
  articleId?:number,
  userId?:number,
  pid?:number,
  target?:string,
  name?:string,
  content?:string,
  url?:string,
  email?:string,
  avatar?:string,
  createDate?:string
}
export interface CommentsRes{
  id?:number,
  articleId?:number,
  userId?:number,
  pid?:number,
  target?:string,
  name?:string,
  content?:string,
  url?:string,
  email?:string,
  avatar?:string,
  articleName?:string,
  createDate?:string
}
export interface CommentsQuery extends PageQuery{
    keyword?:string,
  }