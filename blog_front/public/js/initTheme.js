if (localStorage.getItem("theme") === 'dark' || (!localStorage.getItem("theme") &&
    window.matchMedia("(prefers-color-scheme: dark)"))) {
    // 用户的默认模式是暗黑模式
    document.documentElement.classList.add("dark");
} else {
    // 用户的默认模式是亮色模式
    document.documentElement.classList.remove("dark");
}

