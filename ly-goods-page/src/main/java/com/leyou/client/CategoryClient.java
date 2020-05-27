package com.leyou.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface CategoryClient {
    @GetMapping("category/names")
    List<String> queryNameByIds(@RequestParam("ids") List<Long> ids);
}
