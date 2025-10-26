package com.library.dto.board;

import com.library.entity.board.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
    댓글 조회 DTO
        - 댓글 목록 조회 시 사용
        - Entity -> DTO 변환 메서드 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    
    private Long id;
    private String content;
    private String authorName;      // 작성자 이름
    private String authorEmail;     // 작성자 이메일 (권한 확인용)
    private String createdAt;       // 포맷팅된 작성일시
    private String updatedAt;       // 포맷팅된 수정일시
    
    /**
     * Entity -> DTO 변환 정적 팩토리 메서드
     * 
     * 목적: Comment 엔티티를 화면 표시용 CommentDTO로 변환
     * 
     * 주요 변환 작업:
     * 1. 날짜/시간 포맷팅 (LocalDateTime -> String)
     * 2. 작성자 정보 추출 (Member 엔티티에서 이름, 이메일만 추출)
     * 3. 필요한 필드만 선택적으로 DTO에 담기
     * 
     * 사용 시점:
     * - 댓글 목록 조회 시
     * - 게시글 상세 페이지의 댓글 표시 시
     * 
     * @param comment 변환할 Comment 엔티티 (DB에서 조회한 원본 데이터)
     * @return CommentDTO 화면 표시용 DTO 객체
     */
    public static CommentDTO from(Comment comment) {
        // 날짜/시간 포맷터 생성 (예: "2025-10-21 13:30")
        // 패턴 설명: yyyy(년 4자리) - MM(월 2자리) - dd(일 2자리) HH(시 24시간) : mm(분)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // 빌더 패턴으로 DTO 객체 생성 및 반환
        return CommentDTO.builder()
                .id(comment.getId())                                        // 댓글 ID (수정/삭제 시 식별용)
                .content(comment.getContent())                              // 댓글 내용 (화면에 표시될 텍스트)
                .authorName(comment.getAuthor().getName())                  // 작성자 이름 (Member 엔티티에서 추출)
                .authorEmail(comment.getAuthor().getEmail())                // 작성자 이메일 (수정/삭제 권한 확인용)
                .createdAt(comment.getCreatedAt().format(formatter))        // 작성일시를 문자열로 포맷팅
                .updatedAt(comment.getUpdatedAt().format(formatter))        // 수정일시를 문자열로 포맷팅
                .build();
    }
}
