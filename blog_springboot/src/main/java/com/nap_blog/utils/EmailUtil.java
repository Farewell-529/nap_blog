package com.nap_blog.utils;

import com.nap_blog.entity.Comments;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class EmailUtil {

    @Value("${email.from}")
    public String fromEmail;

    @Value("${email.subject.defaultCommentSubject}")
    public String defaultSubject;

    @Value("${email.subject.replyCommentSubject}")
    public String replySubject;

    @Value("${email.subject.VerificationCodeSubject}")
    public String verificationCodeSubject;

    @Value("${email.content.defaultCommentContent}")
    public String defaultContentTemplate;

    @Value("${email.content.replyCommentContent}")
    public String replyContentTemplate;

    @Value("${email.content.parentCommentContent}")
    public String parentContentTemplate;
    @Autowired
    public JavaMailSender mailSender;

    // 正则表达式匹配 URL
    public static final String URL_PATTERN = "http[s]?://\\S+";
    @Async
    public void sendEmail(String to, String subject, String content,String from)  {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(transferComment(content), true);
            mailSender.send(message);  // 添加发送邮件的调用
        } catch (MessagingException e) {
            e.printStackTrace();  // 或者使用日志记录
        }
    }

    public static String transferComment(String comment) {
        // 创建正则表达式模式
        Pattern pattern = Pattern.compile(URL_PATTERN);
        Matcher matcher = pattern.matcher(comment);
        // 检查是否包含链接
        if (matcher.find()) {
            // 如果有链接，替换链接为 HTML 超链接
            StringBuilder transferComment = new StringBuilder();
            int lastEnd = 0;
            do {
                // 获取链接
                String url = matcher.group();
                // 获取链接前的普通文字
                transferComment.append(comment, lastEnd, matcher.start());
                // 将链接转换为 HTML 超链接
                transferComment.append("<a href='").append(url).append("' target='_blank'>").append(url).append("</a>");
                lastEnd = matcher.end();
            } while (matcher.find());
            // 添加最后部分的普通文字
            transferComment.append(comment.substring(lastEnd));
            return transferComment.toString(); // 返回处理后的评论
        }
        // 如果没有链接，返回普通文字
        return comment;
    }
    public void sendDefaultCommentEmail(Comments comments, String articleTitle) throws MessagingException {
        String defaultContent = String.format(defaultContentTemplate, articleTitle,
                comments.getName(), comments.getContent(), comments.getEmail(), comments.getUrl());
        sendEmail(fromEmail,
                String.format(defaultSubject, articleTitle),
                defaultContent, fromEmail);

    }

    public void sendReplyCommentEmail(String replyCommentEmail, Comments comments, String articleTitle) throws MessagingException {

        String replyContent = String.format(replyContentTemplate, articleTitle,
                comments.getName(), comments.getContent());

       sendEmail(replyCommentEmail,
                String.format(replySubject, articleTitle),
                replyContent,
                fromEmail);
    }

    public void sendParentCommentEmail(String parentCommentEmail, Comments comments, String articleTitle) throws MessagingException {
        String parentContent = String.format(parentContentTemplate, articleTitle,
                comments.getName(), comments.getContent());
        sendEmail(parentCommentEmail,
                String.format(replySubject, articleTitle),
                parentContent,
                fromEmail);
    }
    public void sendVerificationCodeEmail(String verificationCode,String toEmail){
        String content="验证码:"+verificationCode;
        sendEmail(toEmail,verificationCodeSubject,content,fromEmail);

    }
}
