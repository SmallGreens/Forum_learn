package com.mattLearn.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * @author Matt
 * @date 2021/1/26 17:45
 */

@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    // 邮件发送函数
    public boolean sendWithHTMLTemplate(String to, String subject, String template,
                                        Map<String, Object> model){
        try {
            // 发送方的别名
            String nick = MimeUtility.encodeText("Test message");
            InternetAddress from = new InternetAddress(nick + "<test@email.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String res = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                    template, "UTF-8",model);   // 使用模板创建邮件内容
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(res, true);   // 设置邮件内容
            mailSender.send(mimeMessage);
            return true;
        }catch (Exception e){
            logger.error("Mail send failed! " + e.getMessage());
            return false;
        }
    }

    // 设置 发件邮箱的相关参数
    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("xxx@email.com");
        mailSender.setPassword("hahahaha");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
