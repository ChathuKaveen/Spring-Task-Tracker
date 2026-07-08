package com.task_management.Task.Management.repositories;

import com.task_management.Task.Management.entities.Task;
import com.task_management.Task.Management.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task , Long> {
    @Query("""
        SELECT l FROM Task l
        WHERE (:status IS NULL OR l.status = :status)
        AND (:dueDate IS NULL OR l.dueDate = :dueDate) AND (:userId IS NULL OR l.owner.id = :userId)
        """)
    Page<Task> findWithFilters(
            @Param("status") TaskStatus status,
            @Param("dueDate") LocalDate dueDate,
            Pageable pageable,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT l FROM Task l
        WHERE (:status IS NULL OR l.status = :status)
        AND (:dueDate IS NULL OR l.dueDate = :dueDate) AND (l.owner.id = :userId)
        """)
    Page<Task> findMyTasksWithFilter(
            @Param("status") TaskStatus status,
            @Param("dueDate") LocalDate dueDate,
            Pageable pageable,
            @Param("userId") Long userId
    );


    @Query("""
            SELECT COUNT(l) > 0 FROM Task l
            WHERE l.id = :id AND l.owner.id = :ownerId
            """)
    boolean findIfOwner(@Param("ownerId") Long ownerId , @Param("id") Long id);
}
