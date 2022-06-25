package top.iceclean.chatspace.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import top.iceclean.chatspace.po.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户权限认证类
 * @date : 2022-06-25
 * @author : Ice'Clean
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements UserDetails {

    /** 用户信息 */
    private User user;

    /** 用户权限 */
    private List<String> permissions;

    /** 封装后的用户权限 */
    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;

    public UserAuthority(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    /**
     * 重写用户权限获取
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        // 把 permissions 中 String 类型的权限信息封装成 SimpleGrantedAuthority 对象
        authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getUserPass();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
