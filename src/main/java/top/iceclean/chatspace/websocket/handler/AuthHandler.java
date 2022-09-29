package top.iceclean.chatspace.websocket.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.pojo.UserAuthority;
import top.iceclean.chatspace.utils.JwtUtils;
import top.iceclean.chatspace.utils.RedisCache;

import java.util.Objects;

/**
 * 权限校验处理器
 * @author : Ice'Clean
 * @date : 2022-09-29
 */
@Component
public class AuthHandler {

    private static RedisCache redisCache;

    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        AuthHandler.redisCache = redisCache;
    }

    @ChannelHandler.Sharable
    public static class Inbound extends ChannelInboundHandlerAdapter{

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("token 解析失败");
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 校验头部的 Sec-WebSocket-Protocol，里边存放了 token
            String token = ((HttpRequest) msg).headers().get("Sec-WebSocket-Protocol");
            ctx.channel().attr(AttributeKey.valueOf("token")).set(token);

            // 解析 token，校验用户信息
            String userId = JwtUtils.parseJWT(token).getSubject();
            String userObject = redisCache.getCacheObject(RedisKey.USER_LOGIN + userId);
            UserAuthority userAuthority = JSONObject.parseObject(userObject, UserAuthority.class);
            if (Objects.isNull(userAuthority)) {
                System.out.println("用户不存在");
                return;
            }

            // 获取权限信息封装到 Authentication 中
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userAuthority, null, userAuthority.getAuthorities());
            // 存入 SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("用户鉴权成功：" + userAuthority.getUser().getUserName());
            super.channelRead(ctx, msg);
        }
    }

    @ChannelHandler.Sharable
    public static class Outbound extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("== 放回来自 websocket 的token");
            Object token = ctx.channel().attr(AttributeKey.valueOf("token")).get();
            ((DefaultFullHttpResponse) msg).headers().set("Sec-WebSocket-Protocol", token);
            super.write(ctx, msg, promise);
        }
    }
}
