package com.library.comment;

import com.library.entity.board.Board;
import com.library.entity.board.Comment;
import com.library.entity.board.CommentStatus;
import com.library.entity.member.Member;

import java.time.LocalDateTime;

/**
 * Comment 엔티티 테스트 생성 헬퍼 클래스
 * 
 * 목적: 테스트에서 Comment 엔티티를 쉽게 생성하고 BaseEntity 필드를 설정
 * 
 * 장점:
 * 1. 테스트 코드의 가독성 향상 (복잡한 리플렉션 로직을 헬퍼로 분리)
 * 2. 재사용성 증가 (모든 테스트에서 동일한 방식으로 Comment 생성 가능)
 * 3. 유지보수 용이 (BaseEntity 구조 변경 시 한 곳만 수정)
 * 
 * 사용 시나리오:
 * - 단위 테스트에서 Comment 엔티티가 필요한 경우
 * - DB를 사용하지 않고 순수 Java 객체로 테스트하는 경우
 * - createdAt, updatedAt 값을 명시적으로 설정해야 하는 경우
 */
public class CommentTestBuilder {

    /**
     * 기본값으로 Comment 엔티티 생성
     * 
     * 특징:
     * - 현재 시간으로 createdAt, updatedAt 자동 설정
     * - ACTIVE 상태로 설정
     * - 간단한 테스트에 적합
     * 
     * @param content 댓글 내용
     * @param author 작성자 Member 엔티티
     * @param board 게시글 Board 엔티티
     * @return 생성된 Comment 엔티티 (날짜 필드 포함)
     */
    public static Comment createComment(String content, Member author, Board board) {
        LocalDateTime now = LocalDateTime.now();
        return createCommentWithDates(content, author, board, now, now);
    }

    /**
     * 날짜를 지정하여 Comment 엔티티 생성
     * 
     * 특징:
     * - 생성일시와 수정일시를 명시적으로 설정 가능
     * - 특정 시점의 댓글을 시뮬레이션해야 하는 경우 유용
     * - 날짜 기반 비즈니스 로직 테스트에 적합
     * 
     * @param content 댓글 내용
     * @param author 작성자 Member 엔티티
     * @param board 게시글 Board 엔티티
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @return 생성된 Comment 엔티티 (날짜 필드 포함)
     */
    public static Comment createCommentWithDates(
            String content,
            Member author,
            Board board,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        
        // 1단계: Builder 패턴으로 Comment 기본 속성 설정
        Comment comment = Comment.builder()
                .content(content)                   // 댓글 내용
                .author(author)                     // 작성자
                .board(board)                       // 게시글
                .status(CommentStatus.ACTIVE)       // 상태: 활성
                .build();
        
        // 2단계: 리플렉션으로 BaseEntity의 날짜 필드 설정
        // (BaseEntity는 setter가 없어서 리플렉션으로만 설정 가능)
        setBaseEntityFields(comment, createdAt, updatedAt);
        
        return comment;
    }

    /**
     * ID를 포함하여 Comment 엔티티 생성 (DTO 변환 테스트용)
     * 
     * 특징:
     * - ID 값을 명시적으로 설정
     * - DTO 변환 로직 테스트에 최적화
     * - DB 저장 없이 ID를 가진 엔티티 생성 가능
     * 
     * @param id 댓글 ID
     * @param content 댓글 내용
     * @param author 작성자 Member 엔티티
     * @return 생성된 Comment 엔티티 (ID와 날짜 필드 포함)
     */
    public static Comment createCommentWithId(Long id, String content, Member author) {
        LocalDateTime now = LocalDateTime.now();
        
        // Builder로 Comment 생성 (ID 포함)
        Comment comment = Comment.builder()
                .id(id)                             // ID 설정 (일반적으로는 DB에서 자동 생성)
                .content(content)
                .author(author)
                .status(CommentStatus.ACTIVE)
                .build();
        
        // 날짜 필드 설정
        setBaseEntityFields(comment, now, now);
        
        return comment;
    }

    /**
     * 리플렉션을 사용하여 BaseEntity의 날짜 필드 설정 (private 메서드)
     * 
     * 동작 원리:
     * 1. Comment의 부모 클래스(BaseEntity)에서 필드를 찾음
     * 2. setAccessible(true)로 접근 권한 획득
     * 3. 필드에 값을 직접 설정
     * 
     * 왜 필요한가?
     * - BaseEntity는 JPA Auditing으로 자동 관리되는 필드
     * - setter가 없어서 일반적인 방법으로는 값 설정 불가
     * - 단위 테스트에서는 DB 없이 객체만 생성하므로 자동 설정이 안 됨
     * 
     * 참고:
     * - 이 로직을 헬퍼 클래스로 분리하여 테스트 코드가 깔끔해짐
     * - BaseEntity 구조가 변경되어도 이 메서드만 수정하면 됨
     * 
     * @param comment 날짜를 설정할 Comment 엔티티
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     */
    private static void setBaseEntityFields(
            Comment comment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        try {
            // createdAt 필드 설정
            java.lang.reflect.Field createdAtField = 
                comment.getClass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);     // private 접근 권한 획득
            createdAtField.set(comment, createdAt); // 값 설정
            
            // updatedAt 필드 설정
            java.lang.reflect.Field updatedAtField = 
                comment.getClass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);     // private 접근 권한 획득
            updatedAtField.set(comment, updatedAt); // 값 설정
            
        } catch (NoSuchFieldException e) {
            // 필드를 찾지 못한 경우 (BaseEntity 구조가 변경된 경우)
            throw new RuntimeException("BaseEntity에 필드가 존재하지 않습니다: " + e.getMessage(), e);
            
        } catch (IllegalAccessException e) {
            // 접근이 거부된 경우 (SecurityManager 등의 제약)
            throw new RuntimeException("BaseEntity 필드에 접근할 수 없습니다: " + e.getMessage(), e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            throw new RuntimeException("BaseEntity 필드 설정 중 오류 발생", e);
        }
    }
}
