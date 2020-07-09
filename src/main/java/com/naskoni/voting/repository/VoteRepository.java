package com.naskoni.voting.repository;

import com.naskoni.voting.entity.BlogPostVote;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

/**
 * @author Atanas Atanasov
 * @version 1.0.0
 */
public interface VoteRepository extends JpaRepository<BlogPostVote, Long> {

  /**
   * The chosen DB lock strategy is OPTIMISTIC_FORCE_INCREMENT. PESSIMISTIC_READ was tested
   * succesfully with H2 but with MS SQL Server was failing immediately with {@link
   * CannotAcquireLockException} and there was not possible to tune the server to wait for acquiring
   * a lock with this: {@literal @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout",
   * value = "3000")})}
   */
  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<BlogPostVote> findByBlogPostId(Long id);
}
