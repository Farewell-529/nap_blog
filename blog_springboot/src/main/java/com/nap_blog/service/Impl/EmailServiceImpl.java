package com.nap_blog.service.Impl;

import com.nap_blog.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailServiceImpl implements EmailService {

   @Autowired
   private JavaMailSender mailSender;

   // 正则表达式匹配 URL
   private static final String URL_PATTERN = "http[s]?://\\S+";
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

   public String transferComment(String comment) {
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
}
