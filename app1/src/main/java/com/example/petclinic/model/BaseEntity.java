package com.example.petclinic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * id 필드만 있는 상위 엔티티
 */
@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

  private static final Logger log = LoggerFactory.getLogger(BaseEntity.class);

  /**
   * identity: 기본 키 생성을 데이타베이스에 위임
   * sequence: 데이타베이스의 sequence object를 사용
   * table: 키 생성 전용 테이블을 만들어서 사용
   * auto: dialect에 따라 자동
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @JsonIgnore
  public boolean isNew() {
    //id가 null 이면 persist 아니면 merge
    return this.id == null;
  }

  @PrePersist
  public void prePersist() {
    log.info("PrePersist Event:{}", this);
  }

  @PostPersist
  public void postPersist() {
    log.info("PostPersist Event:{}", this);
  }

  @PreRemove
  public void preRemove() {
    log.info("PreRemove Event:{}", this);
  }

  @PostRemove
  public void postRemove() {
    log.info("PostRemove Event:{}", this);
  }

  @PreUpdate
  public void preUpdate() {
    log.info("PreUpdate Event:{}", this);
  }

  @PostUpdate
  public void postUpdate() {
    log.info("PostUpdate Event:{}", this);
  }

  @PostLoad
  public void postLoad() {
    log.info("PostLoad Event:{}", this);
  }
}
