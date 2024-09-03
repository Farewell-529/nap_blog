export interface User {
    id?: number,
    username: string,
    password: string,
    avatar?: string,
    bio?: string,
    motto?: string,
    githubUrl?: string,
    tgUrl?: string,
    xUrl?: string
}
export interface Account {
    username: string,
    password: string
}