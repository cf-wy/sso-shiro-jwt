package com.example.demo.config;

import com.example.demo.jwt.JwtFilter;
import com.example.demo.realm.JwtRealm;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroFilterConfig {

    @Value("${jwt.login-url}")
    private String loginUrl;
    @Value("${jwt.publicKey}")
    private String publicKey;

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager,OSSConfigProperties ossConfigProperties) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        // 添加casFilter到shiroFilter中
        Map<String, Filter> filters = new HashMap<>();
        /*CasFilter casFilter=new CasFilter();
        LogoutFilter logoutFilter=new LogoutFilter();
        logoutFilter.setRedirectUrl(ossConfigProperties.getServerUrlPrefix()+"/logout?service="+ossConfigProperties.getClientHostUrl());
        //casFilter.setFailureUrl("/");
        filters.put("casFilter", casFilter);
        filters.put("logout",logoutFilter);*/
        filters.put("jwt",new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());
        return shiroFilterFactoryBean;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String,String> paths=new LinkedHashMap<>();
        paths.put("/static/**","anon");
        paths.put("/403","anon");
        paths.put("/jwt/**","jwt");
        paths.put("/logout","logout");
        paths.put("/**","authc");
        chainDefinition.addPathDefinitions(paths);
        return chainDefinition;
    }

    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    @Bean(name = "jwtRealm")
    public JwtRealm jwtRealm(EhCacheManager cacheManager, ApiConfigProperties apiConfigProperties) {
        JwtRealm realm = new JwtRealm();
        realm.setApiConfigProperties(apiConfigProperties);
        realm.setPublicKey(publicKey);
        /*realm.setCasService(ossConfigProperties.getClientHostUrl());
        realm.setCasServerUrlPrefix(ossConfigProperties.getValidationUrlPrefix());*/
        realm.setAuthorizationCacheName("authorizationCache");
        realm.setCacheManager(cacheManager);
        return realm;
    }
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(JwtRealm jwtRealm) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(jwtRealm);
//      <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
        dwsm.setCacheManager(getEhCacheManager());
        // 指定 SubjectFactory
        dwsm.setSubjectFactory(new CasSubjectFactory());
        return dwsm;
    }
}
