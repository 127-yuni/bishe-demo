package haoyu.niubi.community.model;

import lombok.Data;

@Data
public class Notification {
    private Integer id;
    private String notifier;
//    接收者的id
    private  String receiver;
    private  Integer outerId;
    private Integer type;
    private Long gmt_create;
    private Integer status;
    private  String notifierName;
    private  String outerTitle;
}
