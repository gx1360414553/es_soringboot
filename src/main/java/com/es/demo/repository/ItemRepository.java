package com.es.demo.repository;

import com.es.demo.pojo.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemRepository extends ElasticsearchRepository<Item,Long> {

    List<Item> findByPriceBetween(Double begin, Double end);
    List<Item> findByPriceBetween(Double begin, Double end, Pageable page);
}
