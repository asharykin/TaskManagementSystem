package org.effectivemobile.tms.repository;

import org.effectivemobile.tms.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByTaskId(Long taskId, Pageable pageable);
}
