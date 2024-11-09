
// 为 TOC 中的所有链接添加点击事件监听器
export const addTocJump = () => {
    document.querySelectorAll('.toc-container a').forEach(anchor => {
        anchor.addEventListener('click', (event) => {
            event.preventDefault();
            const targetId = anchor.getAttribute('href')?.slice(1); // 获取目标 ID
            const targetElement = document.getElementById(targetId!); // 查找目标元素
            if (targetElement) {
                const offset = 100; // 设置你想要的距离顶部的偏移量，单位为像素
                const elementPosition = targetElement.getBoundingClientRect().top; // 获取目标元素相对于视口顶部的位置
                const offsetPosition = elementPosition - offset; // 计算偏移后的位置
                window.scrollBy({
                    top: offsetPosition,
                    behavior: 'smooth' // 平滑滚动
                });
            }
        });
    });
}

export const highlightTOC = () => {
    const tocLinks = document.querySelectorAll('.toc-container a');
    const scrollPosition = window.scrollY + 150; // 滚动位置的偏移量，用于调整高亮位置
    let currentActiveLink: Element | null = null;
    tocLinks.forEach(link => {
        const targetId = link.getAttribute('href')?.slice(1); // 获取目标 ID
        const targetElement = document.getElementById(targetId!);
        if (targetElement) {
            //offsetTop距离顶部位置的高度
            const targetPosition = targetElement.offsetTop;
            if (scrollPosition >= targetPosition) {
                currentActiveLink = link; // 记录当前需要高亮的链接
            }
        }
    });
    // 移除所有链接的高亮状态
    tocLinks.forEach(link => {
        link.classList.remove('active');
    });

    // 仅高亮当前滚动到的标题对应的链接
    if (currentActiveLink) {
        (currentActiveLink as HTMLElement).classList.add('active');
    }
}

export const calculateScrollPercent = () => {
    const markdownBody = document.querySelector('.markdown-body') as HTMLElement;
    if (markdownBody) {
        // 获取页面滚动高度
        const scrollTop = window.scrollY || document.documentElement.scrollTop;
        // 获取页面内容的总高度
        const scrollHeight = markdownBody.scrollHeight;
        // 获取页面的可见高度
        const clientHeight = markdownBody.offsetTop;
        // 计算滚动的百分比，并去掉小数点
        const scrollPercent = Math.floor((scrollTop / (scrollHeight - clientHeight)) * 100);
        // 当滚动百分比达到或超过100%时，停止进一步计算
        return scrollPercent >= 100 ? 100 : scrollPercent;
    }
    return 0;
}
