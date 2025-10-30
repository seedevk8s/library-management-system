package com.library.entity.board;

import com.library.entity.base.BaseEntity;
import com.library.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;

/*
    게시글 좋아요 Entity
        - 사용자가 게시글에 누른 좋아요 정보를 관리함
        - BaseEntity를 상속받아 생성일시가 자동 관리됨
        - 게시글(Board)와 다대일(N:1) 연관관계를 가짐
        - 사용자(Member)와 다대일(N:1) 연관관계를 가짐
 */
/*
    목적: 한 사용자가 같은 게시글에 여러 번 좋아요를 누르는 것을 DB 레벨에서 방지
    동작: (board_id, member_id) 조합이 중복되면 DB에서 예외 발생
    예시:
    ✅ 사용자1이 게시글A에 좋아요 → OK
    ❌ 사용자1이 게시글A에 또 좋아요 → DB 에러 (중복)
    ✅ 사용자2가 게시글A에 좋아요 → OK (다른 사용자)
 */
@Entity                                    // JPA Entity로 지정 (DB 테이블과 매핑)
@Table(
    name = "board_like",                   // 테이블 이름: board_like
    uniqueConstraints = {                  // 유니크 제약 조건 설정 (중복 좋아요 방지)
        @UniqueConstraint(
            name = "uk_board_member",      // 제약 조건 이름
            columnNames = {"board_id", "member_id"}  // 복합 유니크 키: (board_id, member_id)
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardLike extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /*
        좋아요가 속한 게시글 (Board와 N:1 관계)
            - 하나의 게시글에 여러 좋아요가 달릴 수 있음
            - 지연 로딩(LAZY) 사용
            - 게시글이 삭제되면 좋아요도 함께 삭제됨 (cascade 설정은 Board에서)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    
    /*
        좋아요를 누른 사용자 (Member와 N:1 관계)
            - 지연 로딩(LAZY) 사용
            - 좋아요는 반드시 사용자가 있어야 함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    /*
        연관관계 편의 메서드 - 게시글 설정
            - 양방향 관계 설정 시 사용
     */
    public void setBoard(Board board) {
        this.board = board;
    }
}
