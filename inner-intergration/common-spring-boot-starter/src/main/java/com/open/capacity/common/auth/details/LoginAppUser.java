package com.open.capacity.common.auth.details;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 用户实体绑定spring security
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
public class LoginAppUser extends SysUser implements UserDetails {

    private static final long serialVersionUID = -3685249101751401211L;

    private Set<SysRole> sysRoles;

    private Set<String> permissions;

    /***
     * 权限重写
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new HashSet<>();
        Collection<GrantedAuthority> synchronizedCollection = Collections.synchronizedCollection(collection);
        if (!CollectionUtils.isEmpty(sysRoles)) {
            sysRoles.parallelStream().forEach(role -> {
                if (role.getCode().startsWith("ROLE_")) {
                    synchronizedCollection.add(new SimpleGrantedAuthority(role.getCode()));
                } else {
                    synchronizedCollection.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
                }
            });
        }

        if (!CollectionUtils.isEmpty(permissions)) {
            permissions.parallelStream().forEach(per -> {
                synchronizedCollection.add(new SimpleGrantedAuthority(per));
            });
        }
        return collection;
    }


    @JsonIgnore
    public Collection<? extends GrantedAuthority> putAll(Collection<GrantedAuthority> collections) {
        Collection<GrantedAuthority> collection = new HashSet<>();
        collection.addAll(collections);
        return collection;
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
        return getEnabled();
    }

}
