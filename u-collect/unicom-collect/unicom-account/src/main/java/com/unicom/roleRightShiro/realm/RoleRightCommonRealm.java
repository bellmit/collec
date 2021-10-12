package com.unicom.roleRightShiro.realm;

import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.service.OrgService;
import com.unicom.roleRightShiro.service.UserService;
import com.unicom.roleRightShiro.utils.RightContants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangpeng
 * @version 创建时间：2020年7月30日 下午12:22:18
 * 类说明:
 */
@Slf4j
public class RoleRightCommonRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private OrgService orgService;

    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        //查询用户的权限
        @SuppressWarnings("unchecked")
        Collection<String> permission = (Collection<String>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_PERMISSION);

        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermissions(permission);
        return authorizationInfo;

    }


    /**
     * 认证
     *
     * @param authenticationToken 主体传过来的认证信息
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1.从主体传过来的认证信息中，获得用户名
        String userName = (String) authenticationToken.getPrincipal();
        Map<String, Object> user = this.userService.getUser(userName);
        if (user == null)
            throw new UnknownAccountException("账户不存在！");

        if(Integer.parseInt(user.get("isAdmin")+"")!=1&&user.get("vday")!=null&&Integer.parseInt(user.get("vday")+"")<0){
            throw new ExpiredCredentialsException("账户授权已过有效期,请联系购买授权！");
        }



        if(!Objects.equals(user.get("isAdmin"), RightContants.ADMIN_FLAG)){
            Map<String,Object> root=this.orgService.selectRootId(user.get("orgId"));
            if(root!=null) {
                user.put("rootId", root.get("id"));
                user.put("rootName",root.get("name"));
            }
        }

        // 2.通过用户名到数据库中获取凭证
        String password = (String) user.get("password");
        if (StringUtils.isBlank(password)) {
            return null;
        }

        //3.验证用户名密码正确性

        String uuid = user.get("salt").toString();
        // 加密盐值
        ByteSource salt = ByteSource.Util.bytes(uuid);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, password, salt, "roleRightRealm");
        //4.移除password
        //user.remove("password");
        //5.将user放入session
        SecurityUtils.getSubject().getSession().setAttribute(IConstants.SESSION_USER_INFO, user);
        return authenticationInfo;
    }


    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        credentialsMatcher = (token, info) -> {
            UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
            // 验证时传递的加密盐
            ByteSource salt = ((SimpleAuthenticationInfo) info).getCredentialsSalt();
            // 登录录入的密码
            String password = new String(usernamePasswordToken.getPassword());
            String hashAlgorithName = "SHA-1";

            int count = 500;

            Object s = new SimpleHash(hashAlgorithName, password, salt, count);

            return s.toString().equalsIgnoreCase(info.getCredentials().toString());
        };
        super.setCredentialsMatcher(credentialsMatcher);
    }


}
