package top.iceclean.chatspace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.iceclean.chatspace.utils.RedisCache;


/**
 * 聊天室
 * @date : 2022-05-24
 * @author : Ice'Clean
 */
@SpringBootApplication
@MapperScan("top.iceclean.chatspace.mapper")
public class ChatSpaceApplication {

    private static RedisCache redisCache;
    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        ChatSpaceApplication.redisCache = redisCache;
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatSpaceApplication.class, args);
        // 项目启动时，清空缓存（上一次项目可能意外关闭导致缓存不一致）
        // redisCache.del(redisCache.keys("chatspace:*"));
    }

}
