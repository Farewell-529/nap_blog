package com.nap_blog.utils;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import org.springframework.stereotype.Component;

@Component
public class SensitiveWordUtil {
    private static final SensitiveWordBs wordBs;

    static {
        // 初始化 wordBs
        wordBs = SensitiveWordBs.newInstance()
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(false)
                .enableNumCheck(false)
                .enableEmailCheck(false)
                .enableUrlCheck(false)
                .enableIpv4Check(false)
                .enableWordCheck(true)
                .numCheckLen(8)
                .wordTag(WordTags.none())
                .charIgnore(SensitiveWordCharIgnores.defaults())
                .init();
    }
  public static String replaceSensitiveWord(String content){
       return wordBs.replace(content);
    }
}
