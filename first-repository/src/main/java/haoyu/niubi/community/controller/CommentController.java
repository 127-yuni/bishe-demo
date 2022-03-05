package haoyu.niubi.community.controller;


import haoyu.niubi.community.dto.CommentCreateDTO;
import haoyu.niubi.community.dto.CommentDTO;
import haoyu.niubi.community.dto.ResultDTO;
import haoyu.niubi.community.enums.CommentTypeEnum;
import haoyu.niubi.community.exception.CustomizeErrorCode;
import haoyu.niubi.community.model.Comment;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private  CommentService commentService;
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    //CommentDTO是前端页面获取的值封装成一个对象
    public Object post(@RequestBody CommentCreateDTO commentDTO,
                       HttpServletRequest request){
        User user =(User)request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
    }
        if(commentDTO == null || StringUtils.isBlank(commentDTO.getContent())){
            return  ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setLikeCount(0);
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getUuid());
        commentService.insert(comment,user);
        return ResultDTO.okOf();
    }
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name="id")Integer id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id);
        return  ResultDTO.okOf(commentDTOS);
    }
  /*
      *@Description  评论点赞
      **/
  @PostMapping("/comment/up")
  @ResponseBody
    public Object commentUp(@RequestBody Comment comment, HttpServletRequest request){
      User user = (User)request.getSession().getAttribute("user");
      if(user == null){
         return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
      }
      commentService.upComment(comment.getId().toString());
        return ResultDTO.okOf();
    }

}
