package haoyu.niubi.community.mapper;


        import haoyu.niubi.community.model.User;
        import org.apache.ibatis.annotations.*;
        import org.springframework.stereotype.Repository;
        import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    @Insert("insert into user (UUID,USERNAME,TOKEN,AVATAR)values(#{uuid},#{username},#{token},#{avatar})")
    void insert(User user);
    @Select("select * from user where UUID = #{uuid}")
    User findByUuid(@Param("uuid") String uuid);
    @Update("update user set TOKEN=#{token},NAME=#{name},AVATAR=#{avatar} where UUID=#{uuid}")
    void update(User user1);
    @Select({
            "<script>",

            "select * from user where UUID  in ",

            "<foreach collection='s' item='item' open='(' separator=',' close=')'>",

            "#{item}",

            "</foreach>",

            "order by UUID",
            "</script>"

    })
    List<User> selectByUser(@Param("s") List<String> s);

    @Select("select username from user where uuid =#{uuid} ")
    String selectUsername(@Param("uuid") String uuid);
    @Update("update comment set like_count = like_count+1 where id = #{commentId}")
    void upComment(@Param("commentId") String commentId);
    @Select("select count(1) from user where mail = #{mail}")
    Integer existMail(@Param("mail")String mail);
    @Select("select uuid from user where mail = #{mail}")
    String selectUUIDByMail(String mail);
}