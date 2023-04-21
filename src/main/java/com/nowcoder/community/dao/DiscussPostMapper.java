package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 分页查询 offset起始行行号，limit每页最多显示条数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit); //动态SQL,userId为0时不考虑进去，传userID是为了我的帖子这个功能
    //需要动态拼一个参数(在<if>里使用)，并且该方法有且只有一个参数，这时候需要使用@Param注解起别名
    int selectDiscussPostRows(@Param("userId") int userId);

    // 增加帖子
    int insertDiscussPost(DiscussPost discussPost);

    // 查询帖子
    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);
    // 修改帖子类型
    int updateType(int id, int type);

    // 修改帖子状态
    int updateStatus(int id, int status);

}
