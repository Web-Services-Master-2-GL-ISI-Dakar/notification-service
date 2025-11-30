package sn.ondmoney.notificationservice.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notificationservice.domain.Notification;
import sn.ondmoney.notificationservice.domain.NotificationLog;
import sn.ondmoney.notificationservice.service.dto.NotificationDTO;
import sn.ondmoney.notificationservice.service.dto.NotificationLogDTO;

/**
 * Mapper for the entity {@link NotificationLog} and its DTO {@link NotificationLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationLogMapper extends EntityMapper<NotificationLogDTO, NotificationLog> {
    @Mapping(target = "notification", source = "notification", qualifiedByName = "notificationId")
    NotificationLogDTO toDto(NotificationLog s);

    @Named("notificationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationDTO toDtoNotificationId(Notification notification);
}
