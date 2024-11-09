export const themeStore = defineStore('theme', () => {
    const isDark = ref(false)
    const toggleTheme = () => {
        isDark.value = !isDark.value
        if (isDark.value) {
            localStorage.setItem("theme", "dark")
        } else {
            localStorage.setItem("theme", "light")
        }
    }
    const initTheme = () => {
        const temp = localStorage.getItem("theme")
        if (temp!=null&&temp == "dark") {
            isDark.value = true
        }
    }
    return {
        isDark,
        toggleTheme,
        initTheme
    }
},
    {
        persist: true
    }
)
