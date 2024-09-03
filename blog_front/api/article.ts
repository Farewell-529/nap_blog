import request from '~/utils/request'
import { type Article ,type ArticleQuery} from '~/types/Article'
import { type Result } from '~/types/Result'

export function articleListApi(params:ArticleQuery): Promise<Result> {
    return request(`/articles/list`, {
        method: 'get',
        params
    })
}
export function articleByIdApi(id:number): Promise<Result> {
    return request(`/articles/${id}`, {
        method: 'get',
    })
}