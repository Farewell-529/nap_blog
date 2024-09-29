import { type Tags } from '~/types/Tags'
import { type PageQuery } from '~/types/PageQuery'

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
export interface ArticleBackRes {
    id?: number
    title?: string,
    categoryName?: string,
    createDate?: string,
    updateDate?:string,
    // status?: string,
}
export interface ArticleInfoRes{
    id?: number;
    title?: string;
    content?: string;
    categoryName?: string;
    tags?: Tags[];
    createDate?: string;
    updateDate?: string;
    viewCount?: number;
}
export interface ArticleQuery extends PageQuery {
    //关键字
    keyword?: string
    categoryId?:number
} 