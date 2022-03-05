package haoyu.niubi.community.mapper;

import haoyu.niubi.community.dto.CommentDTO;
import haoyu.niubi.community.model.Comment;
import haoyu.niubi.community.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@Repository
@Mapper
public interface CommentMapper {
    @Insert("insert into comment(parent_id,gmt_modified,gmt_create,type,like_count,commentator,content)values(#{parentId},#{gmtModified},#{gmtCreate},#{type},#{likeCount},#{commentator},#{content})")
    void insert(Comment comment) ;
    @Select("select * from comment where id=#{parentId}")
    Comment selectByPrimaryKey(@Param("parentId") Integer parentId);
    @Select("select * from comment where parent_id=#{parentId} order by gmt_create desc")
    List<Comment> selectByCommentDTO(@Param("parentId") Integer parentId);
    @Update("update comment set comment_count=#{commentCount}  where id = #{id}")
    void incCommentCount(Comment comment);
    @Select("select  count(1) from comment where parent_id=#{id}")
    Integer selectCommentCount(@Param("id") Integer id);
    @Select("select parent_id from comment where id =#{id}  ")
    String findQuestionId(@Param("id") Integer outerId);
}
