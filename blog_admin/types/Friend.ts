import { type PageQuery } from '~/types/PageQuery'

export interface Friend{
  id?:number,
  friendName?:string,
  url?:string,
  avatarUrl?:string,
  description?:string,
  createDate?:string,
  updateDate?:string
}

export interface FriendQuery extends PageQuery{
    keyword?:string,
    createDate?:string
  }