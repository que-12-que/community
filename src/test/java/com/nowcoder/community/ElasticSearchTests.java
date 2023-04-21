package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testInsert () {
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPostById(241));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPostById(242));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPostById(243));
    }
    @Test
    public void testInsertList() {
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(101, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(102, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(103, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(111, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(112, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(131, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(132, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(133, 0, 100));
        elasticsearchRestTemplate.save(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }
    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        elasticsearchRestTemplate.save(post);
    }
    @Test
    public void testDelete() {
        // 通过id删除
        elasticsearchRestTemplate.delete(String.valueOf(231), DiscussPost.class);
    }
    @Test
    public void testSearch() {
        // 构建搜索条件
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC),SortBuilders.fieldSort("score").order(SortOrder.DESC),SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(nativeSearchQuery, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, nativeSearchQuery.getPageable());
        for (SearchHit<DiscussPost> post : page) {
            System.out.println(post);
//            System.out.println(post.getHighlightFields());
        }
        // 将高亮部分封装到Page里面
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> discussPostSearchHit : page) {
            DiscussPost discussPost = discussPostSearchHit.getContent();
            if (discussPostSearchHit.getHighlightFields().get("title") != null) {
                discussPost.setTitle(discussPostSearchHit.getHighlightFields().get("title").get(0));
            }
            if (discussPostSearchHit.getHighlightFields().get("content") != null) {
                discussPost.setContent(discussPostSearchHit.getHighlightFields().get("content").get(0));
            }
            list.add(discussPost);
        }
        PageImpl<DiscussPost> pageInfo = new PageImpl<DiscussPost>(list, nativeSearchQuery.getPageable(), search.getTotalHits());
        System.out.println(pageInfo.getTotalElements());
        System.out.println(pageInfo.getTotalPages());
        System.out.println(pageInfo.getNumber());
        System.out.println(pageInfo.getSize());
        for (DiscussPost discussPost : pageInfo) {
            System.out.println(discussPost);
        }
    }
    @Test
    public void testSearchByTemplate() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC),SortBuilders.fieldSort("score").order(SortOrder.DESC),SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
//        Page<DiscussPost> page = elasticsearchRestTemplate.

    }
}
