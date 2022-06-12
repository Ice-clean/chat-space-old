package top.iceclean.chatspace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import top.iceclean.chatspace.utils.RedisCache;

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

    private static RedisCache redisCache;
    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        ChatSpaceApplication.redisCache = redisCache;
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatSpaceApplication.class, args);
        // 项目启动时，清空缓存（上一次项目可能意外关闭导致缓存不一致）
        redisCache.del(redisCache.keys("chatspace:*"));
    }

}
