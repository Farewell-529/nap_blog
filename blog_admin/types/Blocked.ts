import { type PageQuery } from '~/types/PageQuery'

export interface Blocked {
    id?: number
    ip: string
    blockReason:string
    status?:number
    createDate?: string
   
}
export interface BlockedQuery extends PageQuery {
    //关键字
    keyword?: string
    createDate?:string
}