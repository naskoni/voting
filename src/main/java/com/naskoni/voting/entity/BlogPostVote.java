package com.naskoni.voting.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blog_post_vote")
public class BlogPostVote extends AbstractEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blog_post_id")
  private BlogPost blogPost;

  @Column(name = "positive_votes")
  private Long positiveVotes = 0L;

  @Column(name = "negative_votes")
  private Long negativeVotes = 0L;
}
