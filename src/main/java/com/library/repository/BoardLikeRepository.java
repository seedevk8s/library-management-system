package com.library.repository;

import com.library.entity.board.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    BoardLike Repository
        - 게시글 좋아요 데이터 접근 계층
        - 좋아요 존재 여부 확인, 조회, 삭제 등의 기능 제공
 */
@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    /*
        특정 게시글에 특정 사용자가 좋아요를 눌렀는지 확인
            - 중복 좋아요 방지를 위해 사용
            - @Param: JPQL의 파라미터와 메서드 파라미터를 명시적으로 매핑

        예시 쿼리:
            SELECT COUNT(*) > 0
            FROM board_like
            WHERE board_id = ? AND member_id = ?
     */
    @Query("SELECT CASE WHEN COUNT(bl) > 0 THEN true ELSE false END " +
           "FROM BoardLike bl " +
           "WHERE bl.board.id = :boardId AND bl.member.id = :memberId")
    boolean existsByBoardIdAndMemberId(
        @Param("boardId") Long boardId,
        @Param("memberId") Long memberId
    );

    /*
        특정 게시글에 특정 사용자가 누른 좋아요 조회
            - 좋아요 취소 시 삭제할 엔티티를 찾기 위해 사용
            - Optional: 좋아요가 없을 수도 있음

        예시 쿼리:
            SELECT *
            FROM board_like
            WHERE board_id = ? AND member_id = ?
     */
    @Query("SELECT bl FROM BoardLike bl " +
           "WHERE bl.board.id = :boardId AND bl.member.id = :memberId")
    Optional<BoardLike> findByBoardIdAndMemberId(
        @Param("boardId") Long boardId,
        @Param("memberId") Long memberId
    );

    /*
        여러 게시글에 대한 특정 사용자의 좋아요 목록 조회
            - 게시글 목록에서 사용자가 좋아요를 누른 게시글을 표시하기 위해 사용

        예시 쿼리:
            SELECT *
            FROM board_like
            WHERE board_id IN (?, ?, ...) AND member_id = ?
     */
    @Query("SELECT bl FROM BoardLike bl " +
           "WHERE bl.board.id IN :boardIds AND bl.member.id = :memberId")
    List<BoardLike> findByBoardIdInAndMemberId(
        @Param("boardIds") List<Long> boardIds,
        @Param("memberId") Long memberId
    );
    
    /*
        특정 게시글의 좋아요 수 카운트
            - Board의 likeCount와 동기화 검증에 사용 가능
            
        예시 쿼리:
            SELECT COUNT(*) 
            FROM board_like 
            WHERE board_id = ?
     */
    @Query("SELECT COUNT(bl) FROM BoardLike bl WHERE bl.board.id = :boardId")
    Long countByBoardId(@Param("boardId") Long boardId);
}
