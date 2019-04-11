package com.es.demo;

import com.es.demo.pojo.Item;
import com.es.demo.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
        list.add(new Item(4L, "华为META12", " 手机", "华为2", 4498.00, "http://image.leyou.com/32.jpg"));
        list.add(new Item(5L, "华为META13", " 手机", "华为3", 4496.00, "http://image.leyou.com/33.jpg"));
        list.add(new Item(6L, "华为META14", " 手机", "华为4", 4495.00, "http://image.leyou.com/34.jpg"));
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


}
