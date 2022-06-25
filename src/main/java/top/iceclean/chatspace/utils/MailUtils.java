package top.iceclean.chatspace.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * @author : Ice'Clean
 * @date : 2021-09-09
 * <p>
 * 发送邮件的工具类
 * 发送邮件的工作异步进行
 */
@Component
public class MailUtils {

    private static JavaMailSender mailSender;

    private static final String SENDER = "1419263382@qq.com";

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        MailUtils.mailSender = mailSender;
    }

    /**
     * 异步发送简单邮件
     *
     * @param to      目标对象数组
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    @Async
    public void sendSimpleMail(String subject, String content, String... to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);

    }

    /**
     * 异步发送支持 HTML 内容的邮件
     *
     * @param to      目标对象数组
     * @param subject 邮件标题
     * @param html    html 文本内容
     */
    @Async
    public void sendHtmlMail(String subject, String html, String... to) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(SENDER);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public static void sendAttachmentsMail(String subject, String content, Map<String, File> files, String... to) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(SENDER);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content);
        Set<Map.Entry<String, File>> fileSet = files.entrySet();
        for (Map.Entry<String, File> f : fileSet) {
            helper.addAttachment(f.getKey(), f.getValue());
        }
        mailSender.send(message);
    }


    public static void sendInlineMail(String subject, String html, Map<String, File> files, String... to) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(SENDER);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        Set<Map.Entry<String, File>> fileSet = files.entrySet();
        for (Map.Entry<String, File> f : fileSet) {
            helper.addInline(f.getKey(), f.getValue());
        }
        mailSender.send(message);
    }
}
