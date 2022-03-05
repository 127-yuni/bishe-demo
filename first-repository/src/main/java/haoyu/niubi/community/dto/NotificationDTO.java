package haoyu.niubi.community.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Integer id;
    private Long gmtCreate;
    private  Integer notifier;
    private  String notifierName;
    private Integer status;
    private String outerTitle;
    private Integer type;
    private  String typeName;
    private  Integer outerId;
}
