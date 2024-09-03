import { type PageQuery } from '~/types/PageQuery'

export interface Category {
    id?: number,
    categoryName?: string,
    createDate?: string
    updateDate?:string
}
export interface CategoryQuery extends PageQuery {
    //关键字
    keyword?: string
}