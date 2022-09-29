package top.iceclean.chatspace.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.filter.JwtAuthenticationTokenFilter;
import top.iceclean.chatspace.pojo.GlobalException;
import top.iceclean.chatspace.utils.Md5Utils;

/**
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    /**
     * 密码使用 MD5 加密
     * @return 密码加密解析器
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // 使用 MD5 加密的密码编码器
        return new PasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(Md5Utils.encode((String) rawPassword));
            }

            @Override
            public String encode(CharSequence rawPassword) {
                return Md5Utils.encode((String)rawPassword);
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭 csrf，允许跨域，不通过 Session 获取 SecurityContext
        http.csrf().disable().cors().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 配置需要认证的接口
        http.authorizeRequests()
                // 允许匿名访问的接口
                .antMatchers("/user/code", "/user/register", "/user/login").anonymous()
                .antMatchers("/log/**", "/css/**", "/js/**").anonymous()
                // 除上边以外的都需要认证
                .anyRequest().authenticated();

        // 添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加认证失败处理器
        http.exceptionHandling().authenticationEntryPoint(
                (request, response, authenticationException) -> {
                    if (authenticationException instanceof InsufficientAuthenticationException) {
                        resolver.resolveException(request, response, null,
                                new GlobalException(ResponseStatusEnum.AUTHENTICATION_ERROR, "Token 为空"));
                        return;
                    }
                    resolver.resolveException(request, response, null, authenticationException);
                }).accessDeniedHandler((request, response, accessDeniedException) -> {
                    resolver.resolveException(request, response, null, accessDeniedException);
                });

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
