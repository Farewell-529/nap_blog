import { type PageQuery } from '~/types/PageQuery'

export interface Tags {
    id?: number,
    tagsName?: string,
    createDate?: string,
    updateDate?:string

}
export interface TagsQuery extends PageQuery {
    //关键字
    keyword?: string
} 