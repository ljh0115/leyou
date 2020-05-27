package com.leyou.search.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.SpecClient;
import com.leyou.entity.Goods;
import com.leyou.entity.SearchResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.mapper.GoodsMapper;
import com.leyou.utils.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecClient specClient;

    public SearchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //构建查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 构建基本查询条件,返回bool查询对象
        QueryBuilder query = buildBasicQueryWithFilter(searchRequest);
        //添加查询条件
        queryBuilder.withQuery(query);
        //spring Data 中所有的分页都是从0开始的
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage() - 1, searchRequest.getDefaultSize()));

        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("categoryAgg").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brandId"));

        List<Category> categories = new ArrayList<>();
        List<Brand> brands = new ArrayList<>();

        //执行查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsMapper.search(queryBuilder.build());

        //获取查询结构
        LongTerms categoryTerms = (LongTerms) goodsPage.getAggregation("categoryAgg");
        List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();
        List<Long> cids = new ArrayList<>();
        buckets.forEach(bucket -> cids.add(bucket.getKeyAsNumber().longValue()));
        List<String> names = categoryClient.queryNameByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Category category = new Category();
            category.setId(cids.get(i));
            category.setName(names.get(i));
            categories.add(category);
        }
        LongTerms brandTerms = (LongTerms) goodsPage.getAggregation("brandAgg");
        for (int i = 0; i < brandTerms.getBuckets().size(); i++) {
            brands.add(brandClient.queryBrandById(brandTerms.getBuckets().get(i).getKeyAsNumber().longValue()));
        }
        //定义specs，将查询结构返回到里面
        List<Map<String, Object>> specs = null;
        //分类唯一才能显示对应分类的规格参数，手机有系统，而衣服没有，所以不能进行规格查询
        if (categories.size() == 1) {

            //获取规格参数，根据分类的id
            specs = getSpecs(categories.get(0).getId(), query);
        }
        return new SearchResult(goodsPage.getTotalElements(), new Long(goodsPage.getTotalPages()), goodsPage.getContent(), categories, brands, specs);
    }

    private List<Map<String, Object>> getSpecs(Long id, QueryBuilder query) {
        List<Map<String, Object>> specList = new ArrayList<>();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //先根据查询条件，执行查询
        queryBuilder.withQuery(query);
        //对规格参数进行聚合，聚合要拿到所有的可搜索的规格参数
        List<SpecParam> searchingSpecParams = this.specClient.querySpecParam(null, id, true, null);
        searchingSpecParams.forEach(specParam -> queryBuilder.addAggregation(
                AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword")));
        AggregatedPage<Goods> page = (AggregatedPage<Goods>) goodsMapper.search(queryBuilder.build());
        searchingSpecParams.forEach(specParam -> {
            String name = specParam.getName();
            StringTerms nameTerms = (StringTerms) page.getAggregation(name);
            List<String> values = new ArrayList<>();
            nameTerms.getBuckets().forEach(bucket -> values.add(bucket.getKeyAsString()));

            Map<String, Object> specMap = new HashMap<>();
            specMap.put("k", name);//key===>CPU型号
            specMap.put("options", values);//value===》["骁龙","联发科","展讯"]
            specList.add(specMap);
        });
        return specList;
    }

    // 构建基本查询条件方法
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
        //创建bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构建基本查询
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));
        //过滤条件构造器
        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
        //整理过滤条件
        Map<String, Object> filter = searchRequest.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            filterQuery.must(QueryBuilders.termQuery(key, value));
        }
        boolQueryBuilder.filter(filterQuery);
        return boolQueryBuilder;
    }

/*老版本
    public PageResult<Goods> search(SearchRequest searchRequest) {
          //判断是否有关键字，没有的话不允许查询所有商品
         String key = searchRequest.getKey();
         if(key==null){
             return null;
         }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
       //对key进行全文搜索查询
         nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
         //查出来的数据不需要全部展示，所以自定义取字段
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[] {"id","skus","subTitle"},null));//excludes：不想显示的参数
        int page = searchRequest.getPage();
        int size = searchRequest.getDefaultSize();
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page-1,size));
        Page<Goods> pageGoods = goodsMapper.search(nativeSearchQueryBuilder.build());
        //Long total, Long totalPage, List<T> items
        return new PageResult<Goods>(pageGoods.getTotalElements(),new Long(pageGoods.getTotalPages()),pageGoods.getContent());
    }*/
}
