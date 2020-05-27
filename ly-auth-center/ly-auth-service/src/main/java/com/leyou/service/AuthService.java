package com.leyou.service;

import com.leyou.client.AuthClient;
import com.leyou.config.JwtProperties;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import pojo.UserInfo;
import utils.JwtUtils;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private AuthClient authClient;

    @Autowired
    private JwtProperties jwtProps;

    public String accredit(String username, String password) {
        try {
            User login = authClient.login(username, password);
            if (login == null) {
                return null;
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setId(login.getId());
            userInfo.setUsername(login.getUsername());
            String token = JwtUtils.generateToken(userInfo, jwtProps.getPrivateKey(), jwtProps.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
