package com.leyou.search.client;

import com.leyou.item.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("item-service")
public interface BrandClient {
    @GetMapping("brand/bid/{bid}")
    Brand queryBrandById(@PathVariable("bid") Long bid);
}
