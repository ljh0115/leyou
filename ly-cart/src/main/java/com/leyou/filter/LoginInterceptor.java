package com.leyou.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import entity.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    private JwtProperties jwtProperties;

    //定义线程域，共享线程，存放登入用户
    public static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public LoginInterceptor(JwtProperties jwtProperties){
         this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

         String token = CookieUtils.getCookieValue(request,jwtProperties.getCookieName());
         if(token==null){
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
             return false;
         }
         try{
           UserInfo userInfo = JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
           threadLocal.set(userInfo);
           return true;
         }catch (Exception e){
               e.printStackTrace();
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
               return false;
         }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }

}
