package haoyu.niubi.community.service;

import haoyu.niubi.community.dto.CommentDTO;
import haoyu.niubi.community.enums.CommentTypeEnum;
import haoyu.niubi.community.enums.NotificationStatusEnum;
import haoyu.niubi.community.enums.NotificationTypeEnum;
import haoyu.niubi.community.exception.CustomizeErrorCode;
import haoyu.niubi.community.exception.CustomizeException;
import haoyu.niubi.community.mapper.CommentMapper;
import haoyu.niubi.community.mapper.NotificationMapper;
import haoyu.niubi.community.mapper.QuestionMapper;
import haoyu.niubi.community.mapper.UserMapper;
import haoyu.niubi.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Transactional
    public void insert(Comment comment, User commentator) {
        //判断回复是否正确
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARM_NOT_FOUND);
        }
        if(comment.getType()==null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARM_WRONG);
        }
        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
          Comment comment1 =  commentMapper.selectByPrimaryKey(comment.getParentId());
          if(comment1 == null){
              throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
          }
            Question question = questionMapper.getById(comment1.getParentId());
            commentMapper.insert(comment);
          //增加评论数
            Comment parentComment = new Comment();
            Integer parentId=comment.getParentId();
            parentComment.setId(parentId);
            parentComment.setCommentCount( commentMapper.selectCommentCount(parentId));
            commentMapper.incCommentCount(parentComment);
            //创建通知
            createNotify(comment, comment1.getCommentator(), commentator.getUsername(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT);
        }else{
               //回复问题
            Question question = questionMapper.getById(comment.getParentId());
            if(question == null){
                throw  new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(question.getCommentCount()+1);
            questionMapper.incCommentCount(question);
            //创建通知
            createNotify(comment, question.getCreator(),commentator.getUsername(),question.getTitle() ,NotificationTypeEnum.REPLY_QUESTION);
        }
    }
    /*
        *@Param   comment 评论
        *                   receiver    被通知者
        *                   notificationType   评论的类型
        *@Return
        *@Description
        **/
    private void createNotify(Comment comment, String receiver, String name, String title, NotificationTypeEnum notificationType) {
        if(receiver == comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmt_create(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(comment.getParentId());
        notification.setNotifier(comment.getCommentator());
        notification.setNotifierName(name);
        notification.setOuterTitle(title);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Integer id) {
        List<Comment> comments=commentMapper.selectByCommentDTO(id);
        if(comments.size() == 0){
            return new ArrayList<>();
        }
        //获取去重评论人
        //Set集合避免重复
        Set<String > commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<String> userIds = new ArrayList<>();
        userIds.addAll(commentators);
        //获取评论人并转化成MAP
        UserExample userExample = new UserExample();
        userExample.setId(userIds);
        List<User> users = userMapper.selectByUser( userExample.getId());
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getUuid, user -> user));
        //转换comment为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO1 = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO1);
            commentDTO1.setUser(userMap.get(comment.getCommentator()));
            return commentDTO1;
        }).collect(Collectors.toList());
        return  commentDTOS;
    }
  /*
      *@Description   评论点赞
      **/

    public void upComment(String commentId) {
        userMapper.upComment(commentId);
    }

      /*
          *@Param   comment的id
          *@Return  问题的id
          *@Description   通过comment 的id找到问题的id
          **/
    public String findQuestionId(Integer outerId) {
        return  commentMapper.findQuestionId(outerId);
    }
}
