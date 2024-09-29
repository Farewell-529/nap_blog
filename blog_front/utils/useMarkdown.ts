// useMarkdown.ts
import { createHighlighter } from 'shiki'
// @ts-ignore
import MarkdownIt from 'markdown-it';
import mathjax3 from 'markdown-it-mathjax3';
import mermaid from '@agoose77/markdown-it-mermaid'
import anchor from 'markdown-it-anchor'
import tocDoneRight from 'markdown-it-toc-done-right';
import { toast } from 'vue-sonner'
export const useMarkdown = () => {
    let md: any;
    let highlighter: any = null; // 缓存 Shiki 实例

    const initializeMarkdown = async () => {
        if (md) {
            return md;  // 如果已经初始化过 md，直接返回
        }
        if (!highlighter) {
            highlighter = await createHighlighter({
                langs: ['javascript', 'typescript', 'vue', 'java', 'go', 'mermaid'],
                themes: ['tokyo-night']
            });
        }

        const slugify = (s: string) => {
            return s.trim()
                .toLowerCase()
                .replace(/\s+/g, '-'); // 替换空格为短横线
        };

        md = new MarkdownIt({
            html: true,
            linkify: true,
            typographer: true,
            highlight(value: string, lang: string) {
                try {
                    let codeHtml = '';
                    if (lang && highlighter.getLoadedLanguages().includes(lang)) {
                        codeHtml = highlighter.codeToHtml(value, {
                            lang,
                            theme: 'tokyo-night'
                        });
                    } else {
                        codeHtml = highlighter.codeToHtml(value, {
                            lang: 'text', // 作为纯文本处理
                            theme: 'tokyo-night'
                        });
                    }
                    const encodedValue = encodeBase64(value);
                    const langLabel = lang ? `<div class="code-lang">${lang}</div>` : '';
                    const copyButton = ` 
                        <svg class="copy-button-svg" data-clipboard-text="${encodedValue}" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                            <path d="M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z" />
                        </svg>
                    `;
                    codeHtml = codeHtml.replace('</pre>', `${langLabel} ${copyButton}</pre>`);
                    return codeHtml;
                } catch (error) {
                    console.error(`Error highlighting code with language '${lang}':`, error);
                    return `<pre><code>${md.utils.escapeHtml(value)}</code></pre>`;
                }
            },
        })
            .use(mathjax3)
            .use(mermaid)
            .use(anchor, {
                level: [1, 2, 3],
                permalinkSymbol: '§',
                slugify
            })
            .use(tocDoneRight, {
                slugify,
                listClass: 'toc-list',
                listItemClass: 'toc-item',
                anchorClass: 'toc-anchor',
                containerClass: 'toc-container',
                includeLevel: [1, 2, 3]
            });
        return md;
    }


    const encodeBase64 = (text: string): string => {
        return btoa(unescape(encodeURIComponent(text)));
    }

    const decodeBase64 = (encodedText: string): string => {
        return decodeURIComponent(escape(atob(encodedText)));
    }
    const handleCopyButtonClick = (event: Event) => {
        if ((event.target as HTMLElement).matches('.copy-button-svg')) {
            const encodedText = (event.target as HTMLElement).getAttribute('data-clipboard-text');
            const decodedValue = decodeBase64(encodedText || '');
            navigator.clipboard.writeText(decodedValue).then(() => {
                toast.success('已复制到粘贴板~')
            }).catch(err => {
                console.error('Failed to copy: ', err);
            });
        }
    }
    return {
        initializeMarkdown,
        handleCopyButtonClick
    }
}
