Nap`s Blog

这是一个前后端分离的博客项目，集成了评论、二级回复、邮箱通知、图片上传等常用功能。系统包含前台展示与后台管理两部分，兼顾用户交互体验和管理效率。页面设计简洁友好，功能完善，专为个人创作者打造，提供高效便捷的博客创作与管理平台。
# 功能
## 后台管理
- jwt登陆验证
- 邮箱找回密码
- 密码存储数据库加密
- 文章管理
- 草稿管理
- 分类管理
- 标签管理
- 友链管理
- 评论管理(支持二级回复、gravatar、评论后博主会被邮件通知、被回复后会被邮件通知、敏感词过滤、拉黑功能)
- 黑名单管理(对违反规则的ip进行封禁、无法再次评论)
- 浏览量查看(支持ip显示、城市显示)
- 网站信息编辑(如修改联系方式、修改密码、修改邮箱、修改自述等)
  ![image](https://github.com/user-attachments/assets/30120526-404f-4d47-ba74-475db77d6469)
## 前台展示
- 支持暗黑模式切换
- 支持文章和友链的免登录评论
- 支持文章侧面标题导航
  ![image](https://github.com/user-attachments/assets/245c91da-b8c0-45ab-94d6-b3e4f55fe850)
  ![image](https://github.com/user-attachments/assets/dbe29e62-6d3b-4933-a336-7466754c4a8e)
# 技术栈
## 前端
- Nuxt.js
- TS
- TailwindCss
## 后端
- Springboot
- MyBatis-plus
- Redis
- MySQL
# 先决条件
- Node.js
- pnpm 
- Redis
# 初始配置
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nap_blog?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  servlet:
    multipart:
      max-file-size: 2MB
      max-request-size: 5MB
  mvc:
    static-path-pattern: /static/**
  web:
    resources:
      static-locations:
        file:C:\\Users\\***\\Desktop\\Nap_Blog\\upload\\avatar\\,file:C:\\Users\\***\\Desktop\\Nap_Blog\\upload\\
#系统邮件设置，用于给用户或博主发送邮件
  mail:
    username: ***@gmail.com
    password: ****
    host: smtp.gmail.com
  data:
    redis:
      host: localhost
      port: 6379
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: classpath:mapper/*.xml
#邮件格式模板
email:
  from: ***@gmail.com
  subject:
    defaultCommentSubject: 标题:《%s》有新评论
    replyCommentSubject: 你在nap博客评论的标题为:《%s》的文章有回复
    VerificationCodeSubject: 博客验证码
  content:
    defaultCommentContent: |
      文章标题:%s<br>
      昵称:%s<br>
      评论内容:%s<br>
      邮箱:%s<br>
      网址：%s<br>
    replyCommentContent: |
      文章标题:%s<br>
      昵称:%s<br>
      回复你说:%s<br>
      博客网址：http://localhost:3000/
    parentCommentContent: |
      文章标题:%s<br>
      昵称:%s<br>
      有回复说:%s<br>
      博客网址：http://localhost:3000/
```
# 使用
## 前端
```
npm i pnpm
pnpm i
pnpm dev
```
## 后端
```
redis-server
```
再启动springboot