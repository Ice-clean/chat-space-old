package top.iceclean.chatspace.config;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Ice'Clean
 * @date : 2022-05-24
 */
@Configuration
public class TomcatConfig {

//
//    @Bean
//    public ServletWebServerFactory webServerFactory() {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
//            //设置解析描述符为true,例如Header 状态码为200,那么解析出来就是200 ok
//            connector.setProperty("sendReasonPhrase", "true");
//            //以下设置为解决RFC7230问题
//            connector.setProperty("relaxedQueryChars", "|{}[](),/:;<=>?@[\\]{}\\");
//            connector.setProperty("relaxedPathChars", "|{}[](),/:;<=>?@[\\]{}\\");
//            //这个属性在tomcat为8.5的时候不生效
//            connector.setProperty("rejectIllegalHeader", "false");
//        });
//
//        return factory;
//    }
}
