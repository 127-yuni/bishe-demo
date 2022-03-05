package haoyu.niubi.community.controller;

import haoyu.niubi.community.dto.CommentDTO;
import haoyu.niubi.community.dto.PaginationDTO;
import haoyu.niubi.community.dto.QuestionDTO;
import haoyu.niubi.community.service.CommentService;
import haoyu.niubi.community.service.QuestionService;
import haoyu.niubi.community.service.UserService;
import haoyu.niubi.community.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class HelloController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/")
    public String index(
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search
    ) {
        PaginationDTO pagination = questionService.list(search,page, size);
        List<Integer> hotIds = redisUtil.lGet("hot", 0, -1).stream().map(o -> (int)o).collect(Collectors.toList());
        List<QuestionDTO> hotQuestions = hotIds.stream().map(o -> questionService.getById(o)).collect(Collectors.toList());
        model.addAttribute("hotQuestions",hotQuestions);
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);

        return "index";
    }
    @GetMapping("/userPage/{uuid}")
    public String UserPage(@PathVariable("uuid")String uuid,Model model){
        return "register";
    }


    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model) {
        //在访问数据库之前用布隆过滤器过滤请求
        QuestionDTO questionDTO = questionService.getById(id);
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id);
        List<QuestionDTO> relatedQuestions=questionService.selectRelated(questionDTO);
        questionService.incView(id);
        Integer viewCount = questionService.selectView(id);
        model.addAttribute("viewCount", viewCount);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
