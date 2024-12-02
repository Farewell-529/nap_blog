export const themeStore = defineStore('theme', () => {
    const isDark = ref(false)
    const toggleTheme = () => {
        isDark.value = !isDark.value
        if (isDark.value) {
            localStorage.setItem("theme", "dark")
            document.documentElement.classList.add("dark");
        } else {
            localStorage.setItem("theme", "light")
            document.documentElement.classList.remove("dark");
        }
    }
    const initTheme = () => {
        if (localStorage.getItem("theme") === 'dark' || (!localStorage.getItem("theme") &&
            window.matchMedia("(prefers-color-scheme: dark)"))) {
                isDark.value = true
        } else {
            isDark.value = false
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
