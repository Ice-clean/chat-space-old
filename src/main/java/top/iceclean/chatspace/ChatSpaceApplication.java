package top.iceclean.chatspace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 聊天室
 * @date : 2022-05-24
 * @author : Ice'Clean
 */
@SpringBootApplication
@MapperScan("top.iceclean.chatspace.mapper")
@ComponentScan(basePackages = {"top.iceclean.chatspace.*", "top.iceclean.logtrace.*"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "top.iceclean.logtrace.config.WebSocketConfig")})
public class ChatSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatSpaceApplication.class, args);
        System.setProperty("druid.mysql.usePingMethod","false");
    }

}
