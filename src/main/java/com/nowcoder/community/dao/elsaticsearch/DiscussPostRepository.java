package com.nowcoder.community.dao.elsaticsearch;

import com.nowcoder.community.entity.DiscussPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> { // 要查的实体类和实体类的主键类型
//    Page<DiscussPost> findDiscussPostByTitleOrContent(String title, String content, Pageable pageable);
}
