import { type PageQuery } from '~/types/PageQuery'
export interface Visits {
    id?: number,
    ip: string,
    city: string,
    fromUrl?: string,
    browser?: string,
    visitTime?: string
}
export interface VisitsQuery extends PageQuery {
    browser?: string
    ip?: string,
    city?: string,
    visitTime?: string,
    fromUrl?: string
} 