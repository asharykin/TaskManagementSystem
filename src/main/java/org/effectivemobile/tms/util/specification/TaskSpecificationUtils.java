package org.effectivemobile.tms.util.specification;

import jakarta.persistence.criteria.Join;
import org.effectivemobile.tms.entity.Task;
import org.effectivemobile.tms.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecificationUtils {

    public static Specification<Task> hasExecutor(Long executorId) {
        return (root, query, criteriaBuilder) -> {
            if (executorId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Task, User> executorJoin = root.join("executor");
            return criteriaBuilder.equal(executorJoin.get("id"), executorId);
        };
    }

    public static Specification<Task> hasAuthor(Long authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Task, User> authorJoin = root.join("author");
            return criteriaBuilder.equal(authorJoin.get("id"), authorId);
        };
    }
}
