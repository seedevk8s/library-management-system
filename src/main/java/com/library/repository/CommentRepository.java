package com.library.repository;

import com.library.entity.board.Comment;
import com.library.entity.board.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
    댓글 Repository - 댓글 데이터에 대한 데이터베이스 접근을 담당
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /*
        특정 게시글의 활성 상태 댓글 목록 조회
            - Fetch Join으로 작성자 정보를 함께 조회하여 N+1 문제 방지
            - 생성일시 오름차순 정렬 (오래된 댓글부터)
     */
    @Query("SELECT c FROM Comment c " +           // Comment 엔티티 조회 (별칭: c)
           "JOIN FETCH c.author " +                // 작성자(Member) 정보를 즉시 로딩 (N+1 문제 방지)
           "WHERE c.board.id = :boardId " +        // 특정 게시글 ID로 필터링
           "AND c.status = :status " +             // 댓글 상태로 필터링 (ACTIVE, DELETED 등)
           "ORDER BY c.createdAt ASC")             // 생성일시 오름차순 정렬 (오래된 댓글 → 최신 댓글)
    List<Comment> findByBoardIdAndStatus(@Param("boardId") Long boardId,  // @Param: JPQL의 :boardId와 매핑
                                         @Param("status") CommentStatus status);  // @Param: JPQL의 :status와 매핑
    
    /*
        특정 게시글의 활성 상태 댓글 개수 조회
            - Spring Data JPA의 쿼리 메서드 네이밍 규칙 사용
            - count: 개수를 반환
            - ByBoardIdAndStatus: board.id와 status 조건으로 필터링
            - 자동으로 SQL의 COUNT 쿼리 생성

        자동 생성되는 SQL
            sqlSELECT COUNT(c.id)
            FROM comment c
            WHERE c.board_id = ?
              AND c.status = ?
     */
    Long countByBoardIdAndStatus(Long boardId,           // 게시글 ID
                                 CommentStatus status);  // 댓글 상태 (ACTIVE, DELETED 등)
}
