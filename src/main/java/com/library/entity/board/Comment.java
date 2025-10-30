package com.library.entity.board;

import com.library.entity.base.BaseEntity;
import com.library.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;

/*
    댓글 Entity
        - 게시글에 대한 댓글 정보를 관리함
        - BaseEntity를 상속받아 생성일시/수정일시가 자동 관리됨
        - 게시글(Board)와 다대일(N:1) 연관관계를 가짐
        - 작성자(Member)와 다대일(N:1) 연관관계를 가짐
 */
@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /*
        댓글 내용
            - 필수 입력 항목
            - 최대 1000자 제한
     */
    @Column(nullable = false, length = 1000)
    private String content;
    
    /*
        댓글이 속한 게시글 (Board와 N:1 관계)
            - 하나의 게시글에 여러 댓글이 달릴 수 있음
            - 지연 로딩(LAZY) 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    
    /*
        댓글 작성자 (Member와 N:1 관계)
            - 지연 로딩(LAZY) 사용
            - 댓글은 반드시 작성자가 있어야 함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;
    
    /*
        댓글 상태
            - ACTIVE: 활성 상태
            - DELETED: 삭제된 상태 (소프트 삭제)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CommentStatus status = CommentStatus.ACTIVE;
    
    /*
        비즈니스 메서드 - 댓글 수정
     */
    public void update(String content) {
        this.content = content;
    }
    
    /*
        비즈니스 메서드 - 댓글 삭제 (소프트 삭제)
     */
    public void delete() {
        this.status = CommentStatus.DELETED;
    }
    
    /*
        연관관계 편의 메서드 - 게시글 설정
     */
    public void setBoard(Board board) {
        this.board = board;
    }
}
