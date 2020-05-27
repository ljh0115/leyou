package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecClient;
import com.leyou.common.utils.JsonUtils;
import com.leyou.entity.Goods;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.mapper.GoodsMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsMapper goodsMapper;

    public Goods buildGoods(Spu spu){
         Goods goods = new Goods();
         //复制spu的属性值
        BeanUtils.copyProperties(spu,goods);
        List<String> names = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //将可以查询的属性值放入all，空格隔开用于搜索
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names,""));
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        List<Map<String,Object>> skuListMap = new ArrayList<>();
        List<Long> prices = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String,Object> map = new HashMap();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.isBlank(sku.getImages())?"":sku.getImages().split(",")[0]);
            skuListMap.add(map);
        });
        goods.setSkus(JsonUtils.serialize(skuListMap));
        goods.setPrice(prices);
        goods.setSpecs(getSpecs(spu));
        return goods;
    }

    private Map<String,Object> getSpecs(Spu spu) {
        //创建一个装筛选参数的map
        Map<String,Object> specsMap = new HashMap<>();
        //获取到所有的可搜索的规格参数
        List<SpecParam> specParams = specClient.querySpecParam(null, spu.getCid3(), true, null);
        //找到参数集合
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //通用规格
        Map<Long,Object> genericMap = JsonUtils.nativeRead(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        //特有规格
        Map<Long,List<String>> specialMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        specParams.forEach(specParam -> {
                   //可筛选参数的id
                   Long id = specParam.getId();
                   //参数的名字
                   String name = specParam.getName();
                   Object value = "其他";
                   //判断是否是通用规格
                   if(specParam.getGeneric()){
                       value = genericMap.get(id);
                       if (null != value && specParam.getNumeric()) {
                           //数值类型可能需要加分段,以及单位
                           value = this.chooseSegment(value.toString(), specParam);
                       }
                   }else {
                       value = specialMap.get(id);
                   }
                   specsMap.put(name,value);
        });
        return specsMap;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {//segment:1000-2000
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();//添加单位
                }
                break;
            }
        }
        return result;
    }

    public void createAndUpdateIndex(Long id) {
        goodsMapper.save(buildGoods(goodsClient.querySpuById(id)));
    }

    /*删除索引
    *  public void createAndUpdateIndex(Long id) {
        goodsMapper.delete(id);
    }
    * */
}
