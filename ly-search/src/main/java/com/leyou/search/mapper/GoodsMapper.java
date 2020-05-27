package com.leyou.search.mapper;

import com.leyou.entity.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsMapper extends ElasticsearchRepository<Goods,Long> {
}
