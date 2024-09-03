import { type User } from "~/types/User"
export const userStore = defineStore('user', () => {
    const user = ref<User>()
    const setUser = (newUser: User) => {
        user.value = newUser
    }
    const removeUser = () => {
        user.value = <User>{}
    }
    return {
        user,
        setUser,
        removeUser
    }
},
    {
        persist: true
    }
)
