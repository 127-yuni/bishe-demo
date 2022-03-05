package haoyu.niubi.community.service;

import com.google.common.collect.Maps;
import haoyu.niubi.community.dto.PaginationDTO;
import haoyu.niubi.community.dto.QuestionDTO;
import haoyu.niubi.community.dto.QuestionQueryDTO;
import haoyu.niubi.community.exception.CustomizeErrorCode;
import haoyu.niubi.community.exception.CustomizeException;
import haoyu.niubi.community.mapper.QuestionMapper;
import haoyu.niubi.community.mapper.UserMapper;
import haoyu.niubi.community.model.Question;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@CacheConfig(cacheNames = "question")
@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;


    public PaginationDTO list(String search, Integer page, Integer size) {
        Integer totalPage;
        Integer totalCount = 0;
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        totalCount = questionMapper.count();
        if (StringUtils.isNotBlank(search)) {
            String[] strings = StringUtils.split(search, " ");
            search = Arrays.stream(strings).collect(Collectors.joining("|"));
            totalCount = questionMapper.countBySearch(questionQueryDTO);
        }
        List<Question> questions;

        questionQueryDTO.setSearch(search);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        if (page == 0) {
            page = 1;
        }

        paginationDTO.setPagination(totalPage, page);
        //size*(page-1)
        Integer offset = size * (page - 1);
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        if (StringUtils.isNotBlank(search)) {
            questions = questionMapper.selectBySearch(questionQueryDTO);
        } else {
            questions = questionMapper.list(offset, size);
        }
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findByUuid(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list1(String userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;

        Integer totalCount = questionMapper.countByUserId(userId);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (totalPage == 0) {
            totalPage = 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        if (page == 0) {
            page = 1;
        }

        paginationDTO.setPagination(totalPage, page);

        //size*(page-1)
        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.listByUserId(userId, offset, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findByUuid(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public List<Question> getAll() {
        return questionMapper.selectAll();
    }


    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        String creatorId = question.getCreator();
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.findByUuid(creatorId);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        Question question1 = questionMapper.getById(question.getId());
        if (question1 == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        } else {
            question.setGmtModified(System.currentTimeMillis());
            int update = questionMapper.update(question);
            if (update != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /*
   // * @CachePut 强制直接执行方法  执行后从数据库里拿到缓存中
   // * 一般用在 插入和修改 的方法上
   // * */
    @CachePut(value = "viewCount", key = "#id")
    public Integer incView(Integer id) {
        Question question = questionMapper.getById(id);
        question.setViewCount(question.getViewCount() + 1);
        questionMapper.updateView(question);
        return question.getViewCount();
    }

    /*
    @Cacheable  如果缓存里有就直接用缓存的，没有则调用方法
    * */
    @Cacheable(value = "viewCount", key = "#id")
    public Integer selectView(Integer id) {
        return questionMapper.selectViewCount(id);
    }

    @CachePut(value = "likeCount", key = "#id")
    public Integer incLike(Integer id) {
        Question question = questionMapper.getById(id);
        question.setLikeCount(question.getLikeCount() + 1);
        questionMapper.updateLikeCount(question);
        return question.getLikeCount();
    }

    @Cacheable(value = "likeCount", key = "#id")
    public Integer selectLike(Integer id) {
        return questionMapper.selectLikeCount(id);
    }


    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {

        Question question = new Question();
        if (StringUtils.isBlank(questionDTO.getTag())) {
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(questionDTO.getTag(), ",");
        String regxp = Arrays.stream(tags).collect(Collectors.joining("|"));
//        String replaceTag = StringUtils.replace(questionDTO.getTag(), ",", "|");
//        question.setTag(replaceTag);
        question.setTag(regxp);
        question.setId(questionDTO.getId());
        List<Question> questionList = questionMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questionList.stream().map(q -> {
            QuestionDTO questionDTO1 = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO1);
            return questionDTO1;
        }).collect(Collectors.toList());
        return questionDTOS;
    }

    /**
     * @description: 计算热度
     */
    public double countHot(Question question) {
        //小时差
        double time = (double) (System.currentTimeMillis() - question.getGmtCreate()) / 3600000;
        double hot = Math.pow((question.getLikeCount() * 0.7 + question.getCommentCount() * 0.3) * 1000 / (time + 2), 1.2);
        return hot;
    }

    /**
     * @description: 热度排行榜
     * @Param： seed  : 热度前几
     */

    public void hot(Long seed) {
        redisUtil.lTrim("hot", 1, 0);
        redisUtil.zRange("hotSet", 0L, seed).stream().forEach(o ->{  redisUtil.lSet("hot", o);} );
    }


}
