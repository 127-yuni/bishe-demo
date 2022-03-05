package haoyu.niubi.community.controller;

import haoyu.niubi.community.dto.NotificationDTO;
import haoyu.niubi.community.enums.NotificationTypeEnum;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.service.CommentService;
import haoyu.niubi.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Integer id) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);

//        如果回复的是问题
        if (NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterId();
        }
        else if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()){//回复的是评论
            String questionId = commentService.findQuestionId(notificationDTO.getOuterId());
            return  "redirect:/question/"+questionId;
        }
        else{
            return "redirect:/";
        }
    }
}
