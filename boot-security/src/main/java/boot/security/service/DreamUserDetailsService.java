package boot.security.service;

import boot.common.SysConstants;
import boot.security.auth.AuthUser;
import boot.system.model.Admin;
import boot.system.model.Role;
import boot.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户详情服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DreamUserDetailsService implements UserDetailsService, UserLockService {
    private final IAdminService adminService;
    private final IRoleService roleService;
    private final IResourceService resourceService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminService.findByName(username);
        if (admin == null) {
            throw new UsernameNotFoundException("username is not found!");
        }
        Integer adminId = admin.getId();
        List<Role> roleList = roleService.findListByAdminId(adminId);
        Set<String> dbAuthsSet = new HashSet<>();
        if (roleList != null && !roleList.isEmpty()) {
            // 获取角色
            loadRoleAuthorities(roleList, dbAuthsSet);
            // 获取资源
            loadUserAuthorities(roleList, dbAuthsSet);
        }
        String password = admin.getPassword();
        boolean enabled = SysConstants.DB_STATUS_OK == admin.getStatus();
        boolean accountNonLocked = SysConstants.DB_ADMIN_NON_LOCKED == admin.getLocked();
        Collection<? extends GrantedAuthority> authorities
                = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));
        // 构造security用户
        return new AuthUser(adminId, username, password, enabled,
                true, true, accountNonLocked, authorities);
    }

    private void loadRoleAuthorities(List<Role> roleList, Set<String> dbAuthsSet) {
        roleList.stream()
                .map(Role::getName)
                .filter(StringUtil::isNotBlank)
                .forEach(x ->
                        dbAuthsSet.add(SysConstants.SECURITY_ROLE_PREFIX + x)
                );
    }

    private void loadUserAuthorities(List<Role> roleList, Set<String> dbAuthsSet) {
        List<Integer> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toList());
        List<String> permissionsList = resourceService.findPermissionsByRoleIds(roleIds);
        permissionsList.stream()
                .filter(StringUtil::isNotBlank)
                .forEach(dbAuthsSet::add);
    }

    @Override
    public boolean updateLockUser(AuthUser authUser) {
        Admin admin = new Admin();
        admin.setId(authUser.getUserId());
        admin.setLocked(SysConstants.DB_ADMIN_LOCKED);
        return adminService.updateById(admin);
    }
}
