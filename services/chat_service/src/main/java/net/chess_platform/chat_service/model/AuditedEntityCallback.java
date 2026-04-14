package net.chess_platform.chat_service.model;

import java.time.OffsetDateTime;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

@Component
public class AuditedEntityCallback implements BeforeSaveCallback<AuditedEntity> {

    @Override
    public AuditedEntity onBeforeSave(AuditedEntity entity, Document document, String collection) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(OffsetDateTime.now());
        }

        entity.setUpdatedAt(OffsetDateTime.now());
        return entity;
    }

}
