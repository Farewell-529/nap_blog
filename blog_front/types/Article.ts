import { type PageQuery } from '~/types/PageQuery'
import { type Tags } from '~/types/Tags'

export interface Article {
    id?: number
    userId?: number,
    title?: string,
    content?: string,
    categoryId?: number,
    createDate?: string,
    status?: string,
    updateDate?: string,
    wordCount?: string
    tagsList?: Array<Tags>
}
export interface ArticleRes {
    id: number
    title: string,
    content:string,
    categoryName: string,
    categoryId: number,
    tagsList:Array<any>,
    viewCount:number,
    createDate: string,
    updateDate:string,
}
export interface ArticleQuery extends PageQuery {
    //关键字
    keyword?: string
    categoryId?:number
    tagsId?:number
} 