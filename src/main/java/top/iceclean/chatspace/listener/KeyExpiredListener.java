package top.iceclean.chatspace.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.websocket.common.MessageSender;

import java.nio.charset.StandardCharsets;

/**
 * Redis 键过期监听器
 * @author : Ice'Clean
 * @date : 2022-06-26
 */
@Component
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    @Qualifier("chatHandler")
    private MessageSender messageSender;

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 获取失效 key 名称
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        // 处理 token 过期事件
        if (key.startsWith(RedisKey.USER_LOGIN)) {
            // 获取用户 ID，并通知该用户
            String userId = key.substring(key.lastIndexOf(":") + 1);
            messageSender.tokenExpiredMsg(Integer.parseInt(userId));
        }
        super.onMessage(message, pattern);
    }
}
