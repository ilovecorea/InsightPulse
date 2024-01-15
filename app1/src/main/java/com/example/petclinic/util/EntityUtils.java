package com.example.petclinic.util;

import com.example.petclinic.model.BaseEntity;
import java.util.Collection;
import org.springframework.orm.ObjectRetrievalFailureException;

public abstract class EntityUtils {

    /**
     * 컬렉션에서 id로 특정 엔티티 조회
     */
    public static <T extends BaseEntity> T getById(Collection<T> entities, Class<T> entityClass, int entityId)
        throws ObjectRetrievalFailureException {
        for (T entity : entities) {
            if (entity.getId() == entityId && entityClass.isInstance(entity)) {
                return entity;
            }
        }
        throw new ObjectRetrievalFailureException(entityClass, entityId);
    }

}
