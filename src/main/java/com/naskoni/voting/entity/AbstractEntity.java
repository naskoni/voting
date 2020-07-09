package com.naskoni.voting.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  @Setter(AccessLevel.PRIVATE)
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", columnDefinition = "datetime", nullable = false, updatable = false)
  private Date created;

  @Setter(AccessLevel.PRIVATE)
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated", columnDefinition = "datetime", nullable = false)
  private Date updated;
}
