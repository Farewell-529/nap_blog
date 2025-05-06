export const themeStore = defineStore('theme', () => {
    const isDark = ref(false)
    const toggleTheme = () => {
        isDark.value = !isDark.value
        if (isDark.value) {
            localStorage.setItem("theme", "dark")
            document.documentElement.classList.add("dark");
            document.documentElement.setAttribute("data-theme", "dark")
        } else {
            localStorage.setItem("theme", "light")
            document.documentElement.classList.remove("dark");
            document.documentElement.setAttribute("data-theme", "light")
        }
    }
    const initTheme = () => {
        const savedTheme = localStorage.getItem("theme") || "light"
        const isDarkMode = savedTheme === "dark"
        isDark.value = isDarkMode
        // 属性在刷新后就会消失
        document.documentElement.setAttribute("data-theme", savedTheme)
        if (isDarkMode) {
            document.documentElement.classList.add("dark");
            document.documentElement.setAttribute("data-theme", "dark")
        } else {
            document.documentElement.classList.remove("dark");
            document.documentElement.setAttribute("data-theme", "light")
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
