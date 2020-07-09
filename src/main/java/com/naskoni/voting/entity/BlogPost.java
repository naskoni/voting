package com.naskoni.voting.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blog_post")
public class BlogPost extends AbstractEntity {

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 1000)
  private String content;

  @Column(nullable = false, length = 50)
  private String author;

  @OneToOne(mappedBy = "blogPost", cascade = CascadeType.ALL)
  private BlogPostVote vote;
}
