package haoyu.niubi.community.service;

import haoyu.niubi.community.dto.NotificationDTO;
import haoyu.niubi.community.dto.PaginationDTO;
import haoyu.niubi.community.enums.NotificationStatusEnum;
import haoyu.niubi.community.enums.NotificationTypeEnum;
import haoyu.niubi.community.exception.CustomizeErrorCode;
import haoyu.niubi.community.exception.CustomizeException;
import haoyu.niubi.community.mapper.NotificationMapper;
import haoyu.niubi.community.mapper.UserMapper;
import haoyu.niubi.community.model.Notification;
import haoyu.niubi.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(String userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        Integer totalPage;

        Integer totalCount = notificationMapper.countByUserId(userId);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if(totalPage == 0){
            totalPage =1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        if(page ==0){
            page =1;
        }

        paginationDTO.setPagination(totalPage, page);

        //size*(page-1)
        Integer offset = size * (page - 1);
        List<Notification> notifications = notificationMapper.listByUserId(userId, offset, size);
        List<NotificationDTO> notificationList = new ArrayList<>();
        if(notifications.size() == 0){
            return paginationDTO;
        }
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationList.add(notificationDTO);
        }

        paginationDTO.setData(notificationList);
        return paginationDTO;
        }

        //通知数（未读）
    public String unreadCount(String uuid) {
        Notification notificationExample = new Notification();
        notificationExample.setReceiver(uuid);
        notificationExample.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        return String.valueOf(notificationMapper.countByExample(notificationExample));
}
  /*
      *@Param  id   通知id
      *
      *@Return
      *@Description
      **/
    public NotificationDTO read(Integer id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getUuid())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}

