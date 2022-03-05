package haoyu.niubi.community.controller;

import com.google.common.hash.Funnel;
import haoyu.niubi.community.cache.TagCatch;
import haoyu.niubi.community.filter.BloomFilterHelper;
import haoyu.niubi.community.mapper.QuestionMapper;
import haoyu.niubi.community.model.Question;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.service.QuestionService;
import haoyu.niubi.community.utils.RedisUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;
    @Resource
    private RedisUtil redisUtil;
    private static  int id= 1;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Integer id,
                       Model model) {
        Question question = questionMapper.getById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", question.getId());
        model.addAttribute("tags", TagCatch.get());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags",TagCatch.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            HttpServletRequest request, Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCatch.get());
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }
        String invalid =TagCatch.filterInvalid(tag);
        if(StringUtils.isNotBlank(invalid)) {
            model.addAttribute("error", "输入非法标签" +invalid);
            return "publish";
        }

        String lastTag = tag.substring(tag.length() - 1);
        if(lastTag.equals(",")||lastTag.equals("，")){
            model.addAttribute("error","标签格式不正确");
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error", "描述不能为空");
            return "publish";
        }
        if (title == null || title == "") {
                model.addAttribute("error", "标题不能为空");
                return "publish";
        }

        Question question = new Question();
        String replaceTag = StringUtils.replace(tag, "，", ",");
        question.setTag(replaceTag);
        question.setTitle(title);
        question.setDescription(description);
        question.setCreator(user.getUuid());
        question.setId(++id);
        questionService.createOrUpdate(question);
        //在访问数据库之前用布隆过滤器过滤请求
        try{
            BloomFilterHelper<Integer> bloomFilterHelper =
                    new BloomFilterHelper<>((Funnel<Integer>)(from, into) ->
                            into.putInt(from).putInt(from),
                            1500000, 0.00001);
            redisUtil.addByBloomFilter(bloomFilterHelper,"id_bloom",id);
        }catch (Exception e){
            System.out.println("error");
            return  "error";
        }
        return "redirect:/";
    }
}
