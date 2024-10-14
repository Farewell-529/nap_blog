export interface Comments {
    id?: number,
    targetId:number,
    userId?:number,
    pid:number,
    replyId:number,
    targetType:string,
    name:string,
    content:string,
    email:string,
    avatar?:string,
    url:string,
    createDate?: string,
    childComments?:Array<Comments>
}
export interface CommentsRes {
    id?: number,
    userId?:number,
    pid?:number,
    replyName?:string,
    name?:string,
    content?:string,
    avatar?:string,
    createDate?: string,
    childComments?:Array<CommentsRes>
}