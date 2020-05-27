package com.leyou.test;

import com.leyou.SearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.IndexService;
import com.leyou.item.bo.SpuBo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class IndexCreateTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private IndexService indexService;

    @Autowired
    private GoodsRepository goodsRepository;

    /**
     * 创建索引库、表
     */
    @Test
    public void index() {
        // 建库
        elasticsearchTemplate.createIndex(Goods.class);
        // 建表
        elasticsearchTemplate.putMapping(Goods.class);
    }

    /**
     * 将数据加载到 goods 库中
     */
    @Test
    public void loadData() {
        int page = 1;
        // 不断查询数据库导入索引库
        while (true) {
            // 查询 spu 数据，每页查询 50 条数据
            PageResult<SpuBo> pageResult = goodsClient.querySpuByPage(null, null, page, 50);
            if (pageResult == null) {
                break;
            }
            page++;
            // 将 spu 数据转换为 goods
            List<Goods> goodsList = new ArrayList<>();
            pageResult.getItems().forEach(spuBo -> {
                Goods goods = indexService.buildGoods(spuBo);
                goodsList.add(goods);
            });
            // 批量保存到索引库中
            goodsRepository.saveAll(goodsList);
        }
    }

}
