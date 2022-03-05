package haoyu.niubi.community.model;

import lombok.Data;

@Data
public class Question {
    private  Integer id ;
    private  String title;
    private  String description;
    private  String tag;
    private  Long gmtCreate;
    private  Long gmtModified;
    private  String creator;
    private  Integer viewCount;
    private  Integer commentCount;
//    问题点赞数
    private  Integer likeCount;
}
