package com.gzyijian.elasticsearchdemo.restcontroller;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zmjiangi
 * @date 2019-5-6
 */
@RestController
public class NovelRestController {

    @Autowired
    private TransportClient client;

    @GetMapping("/book/novel/get/{id}")
    public ResponseEntity get(@PathVariable String id) {
        GetResponse result = this.client.prepareGet("book", "novel", id).get();
        if (!result.isExists()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/book/novel/add")
    public ResponseEntity add(
            String title,
            String author,
            String wordCount,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date publishDate
    ) throws Exception {
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", title)
                .field("author", author)
                .field("word_count", wordCount)
                .field("publish_date", publishDate);
        IndexResponse indexResponse = this.client.prepareIndex("book", "novel").setSource(xContentBuilder).get();

        return ResponseEntity.ok(indexResponse);
    }

    @PostMapping("/book/novel/delete/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        DeleteResponse result = this.client.prepareDelete("book", "novel", id).get();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/book/novel/update")
    public ResponseEntity update(String title, String author) throws Exception {
        UpdateRequest updateRequest = new UpdateRequest("book", "novel", "1");

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject();

        if (StringUtils.hasText(title)) {
            xContentBuilder.field("title", "title");
        }
        if (StringUtils.hasText(author)) {
            xContentBuilder.field("author", "author");
        }
        updateRequest.doc(xContentBuilder);
        UpdateResponse updateResponse = this.client.update(updateRequest).get();
        return ResponseEntity.ok(updateResponse);
    }

    @GetMapping("/book/novel/query")
    public ResponseEntity query(
            String title,
            String author,
            @RequestParam(required = false, defaultValue = "0") Integer gtWordCount,
            @RequestParam(required = false, defaultValue = "0") Integer ltWordCount
    ) {
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.hasText(title)) {
            booleanQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }
        if (StringUtils.hasText(author)) {
            booleanQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        }

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("word_count").from(gtWordCount);
        if (ltWordCount > 0) {
            rangeQueryBuilder.to(ltWordCount);
        }

        booleanQueryBuilder.filter(rangeQueryBuilder);

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(booleanQueryBuilder)
                .setSize(10);
        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> result = new ArrayList<>();
        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit hit : searchHits) {
            result.add(hit.getSourceAsMap());
        }
        return ResponseEntity.ok(result);
    }

}
