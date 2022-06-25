package top.iceclean.chatspace.filter;

import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.pojo.GlobalException;
import top.iceclean.chatspace.pojo.UserAuthority;
import top.iceclean.chatspace.utils.JwtUtils;
import top.iceclean.chatspace.utils.RedisCache;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 提取token
        String token = request.getHeader("token");
        // 如果token为空需要放行，因为在访问security的匿名接口时过滤器同样起作用，而此时 token 值为空
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析 token
        String userId;
        try {
            userId = JwtUtils.parseJWT(token).getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("JWT 解析失败");
            resolver.resolveException(request,response,
                    null,new GlobalException(ResponseStatusEnum.AUTHENTICATION_ERROR, "Token 非法"));
            return;
        }

        // 校验用户信息
        String redisKey = RedisKey.USER_LOGIN + userId;
        UserAuthority userAuthority = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(userAuthority)) {

            log.error("JWT 对应的用户不存在");
            resolver.resolveException(request,response,
                    null,new GlobalException(ResponseStatusEnum.AUTHENTICATION_ERROR, "Token 已过期"));
            return;
        }

        // 获取权限信息封装到 Authentication 中
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userAuthority, null, userAuthority.getAuthorities());
        // 存入 SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info("封装用户 {} 权限：{}", userAuthority.getUser().getUserName(), userAuthority.getAuthorities().toString());

        // 放行
        filterChain.doFilter(request, response);
    }
}
