package haoyu.niubi.community.mapper;

import haoyu.niubi.community.model.Notification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NotificationMapper {
    @Insert("insert into notification(notifier,receiver,outerId,type,gmt_create,status,outer_title,notifier_name)values(#{notifier},#{receiver},#{outerId},#{type},#{gmt_create},#{status},#{outerTitle},#{notifierName})")
    void insert(Notification notification);
    @Select("select count(1) from notification where receiver = #{userId}")
    Integer countByUserId(String userId);
    @Select("select * from notification where receiver = #{userId} limit #{offset},#{size} ")
    List<Notification> listByUserId(String userId, Integer offset, Integer size);
    @Select("select count(1) from notification where receiver = #{receiver} and status =#{status}")
    Integer countByExample(Notification notificationExample);
    @Select("select * from notification where id = #{id}")
    Notification selectByPrimaryKey(Integer id);
    @Update("update notification set status = #{status} where id = #{id}")
    void updateByPrimaryKey(Notification notification);
}
