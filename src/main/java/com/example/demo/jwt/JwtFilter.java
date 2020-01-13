package com.example.demo.jwt;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
@Slf4j
public class JwtFilter extends AuthenticatingFilter {
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest= WebUtils.toHttp(request);
        String token=httpRequest.getParameter("id_token");
        if(StringUtils.isEmpty(token)){
            return null;
        }
        return new JwtToken(token);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
       /* String redirectUrl=WebUtils.toHttp(request).getParameter("redirect_url");
        if(!StringUtils.isEmpty(redirectUrl)){
            setSuccessUrl(redirectUrl);
        }*/
        issueSuccessRedirect(request, response);
        return false;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        // is user authenticated or in remember me mode ?
        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                issueSuccessRedirect(request, response);
            } catch (Exception e1) {
                log.error("Cannot redirect to the default success url", e1);
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, "/error");
            } catch (IOException e2) {
                log.error("Cannot redirect to failure url : {}", "/error", e2);
            }
        }
        return false;
    }
}
