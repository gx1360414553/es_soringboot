package com.es.demo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.es.demo.pojo.Item;
import com.es.demo.repository.ItemRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    ElasticsearchTemplate template;
    @Autowired
    ItemRepository itemRepository;

    @Test
    public void testCreate() {
        template.createIndex(Item.class);
        template.putMapping(Item.class);

    }

    @Test
    public void indexList() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Item(3L, "华为META11", " 手机", "华为1", 4499.00, "http://image.leyou.com/31.jpg"));
        list.add(new Item(3L, "华为5META11", " 手机", "华为5", 4499.00, "http://image.leyou.com/31.jpg"));
        list.add(new Item(4L, "苹果手机A12", " 手机", "华为2", 4498.00, "http://image.leyou.com/32.jpg"));
        list.add(new Item(5L, "三星手机B13", " 手机", "华为3", 4496.00, "http://image.leyou.com/33.jpg"));
        list.add(new Item(6L, "小米手机8", " 手机", "华为4", 4495.00, "http://image.leyou.com/34.jpg"));
        list.add(new Item(7L, "小米手机8", " 手机", "小米", 4495.00, "http://image.leyou.com/34.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);
    }
    @Test
    public void teatFind() {
        Iterable<Item> all = itemRepository.findAll();
        for (Item item : all) {
            System.out.println(item);
        }
    }
    @Test
    public void teatFindBy() {
        List<Item> all = itemRepository.findByPriceBetween(3699d,4496d);
        for (Item item : all) {
            System.out.println(item);
        }
    }
    @Test
    public void teatFindByPage() {
        //page总是从0开始，表示查询页，size指每页的期望行数。
        Pageable page = PageRequest.of(0, 2);
        List<Item> all = itemRepository.findByPriceBetween(3699d,4496d,page);
        for (Item item : all) {
            System.out.println(item);
        }
    }

    @Test
    public void teatQuery() {
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","price","title"}, null));
        //查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机"));
        //QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", "小米手机");
        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //分页
        queryBuilder.withPageable(PageRequest.of(1,2));

        Page<Item> page = itemRepository.search(queryBuilder.build());
        long total = page.getTotalElements();
        System.out.println("total = " + total);
        int totalPages = page.getTotalPages();
        System.out.println("totalPages = " + totalPages);
        List<Item> content = page.getContent();
        for (Item item : content) {
            System.out.println(item);
        }
    }
    //聚合
    @Test
    public void testAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        String aggName = "popularBrand";
        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(aggName).field("brand"));

        //查询并返回聚合结果
        AggregatedPage<Item> result = template.queryForPage(queryBuilder.build(), Item.class);

        //解析聚合
        Aggregations aggs = result.getAggregations();

        //获取指定名称的聚合
        StringTerms terms = aggs.get(aggName);

        //获取桶
        List<StringTerms.Bucket> buckets = terms.getBuckets();

        for (StringTerms.Bucket bucket : buckets) {
            System.out.println("Key = " + bucket.getKeyAsString());
            System.out.println("DocCount = " + bucket.getDocCount());
        }

    }
    //高亮
    @Test
    public void teatHighlight() {
        String preTag = "<font color='#dd4b39'>";//google的色值
        String postTag = "</font>";
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","price","title","brand"}, null));
        //查询条件
        //queryBuilder.withQuery(QueryBuilders.matchQuery("title", "华为"));
        //queryBuilder.withQuery(QueryBuilders.matchQuery("title", "坚果手机R1"));
        queryBuilder
                .withQuery(QueryBuilders.multiMatchQuery("小米","brand","title"));
        //QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", "小米手机");
        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //分页
        NativeSearchQuery searchQuery = queryBuilder.withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag),
                        new HighlightBuilder.Field("brand").preTags(preTag).postTags(postTag))
                        .build();


        // 高亮字段
        AggregatedPage<Item> page = template.queryForPage(searchQuery, Item.class, new SearchResultMapper() {

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<Item> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    Item item = new Item();
                    item.setId(Long.valueOf(searchHit.getId()));
                    //name or memoe
                    HighlightField title = searchHit.getHighlightFields().get("title");
                    if (title != null) {
                        item.setTitle(title.fragments()[0].toString());
                    }
                    HighlightField brand = searchHit.getHighlightFields().get("brand");
                    if (brand != null) {
                        item.setBrand(brand.fragments()[0].toString());
                    }

                    chunk.add(item);
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }
        });

        List<Item> content = page.getContent();
        for (Item item : content) {
            System.out.println("item = " + item);
        }
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    @Test
    public void testJson() {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("小米");
        String s = JSONObject.toJSONString(Collections.singletonList(item), SerializerFeature.WriteNullStringAsEmpty);
        System.out.println(s);
    }
}
