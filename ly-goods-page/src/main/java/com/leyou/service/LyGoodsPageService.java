package com.leyou.service;

import com.leyou.client.GoodsClient;
import com.leyou.client.SpecClient;
import com.leyou.item.pojo.Spu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LyGoodsPageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    public Map<String, Object> toProductDetail(Long spuId) {
        Map<String, Object> map = new HashMap<>();
        /* let spu = /*[[${spu}]]{};
        let spuDetail = /*[[${spuDetail}]] {};
        let skus = /*[[${skus}]]{};
        let specParams = /*[[${specParams}]] {};
        let specGroups = /*[[${specGroups}]]{};
        */
       Spu spu = goodsClient.querySpuById(spuId);
       map.put("spu",spu);
       map.put("spuDetail",goodsClient.querySpuDetailBySpuId(spuId));
       map.put("skus",goodsClient.querySkuBySpuId(spuId));
       Map<Long,String> specMap = new HashMap<>();
        specClient.querySpecParam(null,spu.getCid3(),null,false)
                .forEach(specParam -> specMap.put(specParam.getId(),specParam.getName()));
       map.put("specParams",specMap);
       map.put("specGroups",specClient.querySpecGroups(spu.getCid3()));
       return map;
    }
}
