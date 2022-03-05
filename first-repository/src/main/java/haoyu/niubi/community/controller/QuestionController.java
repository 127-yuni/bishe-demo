package haoyu.niubi.community.controller;

import haoyu.niubi.community.service.CommentService;
import haoyu.niubi.community.service.QuestionService;
import haoyu.niubi.community.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.annotation.Resource;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Resource
    private RedisUtil redisUtil;

    @GetMapping("/question/giveLike/{id}")
    public String giveLikes(@PathVariable(name = "id") Integer id, Model model) {
        Integer likes = questionService.incLike(id);
        model.addAttribute("likes",likes);
        return "question";
    }






}
