package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.entity.Cart;
import com.leyou.filter.LoginInterceptor;
import entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.J;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX="ly:cart:uid:";

    public void addCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.threadLocal.get();
        BoundHashOperations<String,Object,String> hashOperations = template.boundHashOps(KEY_PREFIX+userInfo.getId());
        String s = hashOperations.get(cart.getSkuId().toString());
        if(s!=null){
            Cart cart1 = JsonUtils.nativeRead(s, new TypeReference<Cart>() {
            });
            cart1.setNum(cart1.getNum()+cart.getNum());
            hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart1));
        }else {
            hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
        }
    }

    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.threadLocal.get();
        BoundHashOperations<String,Long,String> hashOperations = template.boundHashOps(KEY_PREFIX+userInfo.getId());
        List<String> values = hashOperations.values();
        List<Cart> carts = new ArrayList<>();
        if(values!=null){
           values.forEach(value->carts.add(JsonUtils.nativeRead(value, new TypeReference<Cart>() {
           })));
           return carts;
        }else {
            return null;
        }
    }

    public void updateIncrementCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.threadLocal.get();
        BoundHashOperations<String,Object,String> hashOperations = template.boundHashOps(KEY_PREFIX+userInfo.getId());
        cart.setNum(cart.getNum()+1);
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(Long skuId) {
        UserInfo userInfo = LoginInterceptor.threadLocal.get();
        BoundHashOperations<String,Object,String> hashOperations = template.boundHashOps(KEY_PREFIX+userInfo.getId());
        hashOperations.delete(skuId.toString());
    }
}
