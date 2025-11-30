package sn.ondmoney.notificationservice.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notificationservice.domain.NotificationTemplate;
import sn.ondmoney.notificationservice.service.dto.NotificationTemplateDTO;

/**
 * Mapper for the entity {@link NotificationTemplate} and its DTO {@link NotificationTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper extends EntityMapper<NotificationTemplateDTO, NotificationTemplate> {}
