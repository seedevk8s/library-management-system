package com.library.comment;

import com.library.entity.board.Board;
import com.library.entity.board.Comment;
import com.library.entity.board.CommentStatus;
import com.library.entity.member.Member;
import com.library.entity.member.MemberStatus;
import com.library.entity.member.MemberType;
import com.library.entity.member.Role;
import com.library.repository.BoardRepository;
import com.library.repository.CommentRepository;
import com.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CommentRepository 테스트 클래스
 * 
 * 목적: 댓글 리포지토리의 데이터베이스 접근 메서드들이 올바르게 동작하는지 검증
 * 
 * 주요 테스트 항목:
 * - findByBoardIdAndStatus: 게시글 ID와 상태별 댓글 조회
 * - countByBoardIdAndStatus: 게시글 ID와 상태별 댓글 개수 조회
 * 
 * 사용 기술:
 * - @DataJpaTest: JPA 관련 컴포넌트만 로드하는 슬라이스 테스트 (Spring Boot Test의 경량화 버전)
 * - @AutoConfigureTestDatabase: 실제 DB 대신 H2 In-Memory DB를 사용하여 테스트 격리
 * - H2 Database: 테스트용 임베디드 데이터베이스 (메모리에서 실행되어 빠르고 독립적)
 * - 각 테스트 메서드 실행 후 트랜잭션 자동 롤백으로 테스트 간 데이터 독립성 보장
 */
@DataJpaTest  // JPA 리포지토리 테스트를 위한 어노테이션 (Entity, Repository만 로드)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)  // application.yml에 설정된 DB(MySQL, PostgreSQL 등)를 무시하고, 테스트용 embedded database로 교체해라
// 설정된 DB(MySQL)를 테스트용 embedded DB(H2)로 교체
// Replace.ANY: 어떤 DataSource든 classpath에 있는 embedded DB로 교체 (H2 > Derby > HSQLDB 순서로 선택)
class CommentRepositoryTest {

    // 테스트 대상: 댓글 리포지토리
    @Autowired
    private CommentRepository commentRepository;

    // 테스트 데이터 생성용: 게시글 리포지토리 (댓글은 게시글에 종속)
    @Autowired
    private BoardRepository boardRepository;

    // 테스트 데이터 생성용: 회원 리포지토리 (댓글과 게시글은 작성자 필요)
    @Autowired
    private MemberRepository memberRepository;

    // 테스트용 공통 데이터: 댓글 작성자 (모든 테스트에서 재사용)
    private Member testAuthor;
    
    // 테스트용 공통 데이터: 댓글이 달릴 게시글 (모든 테스트에서 재사용)
    private Board testBoard;

    /**
     * 각 테스트 메서드 실행 전에 자동으로 실행되는 초기화 메서드
     * 
     * 목적: 모든 테스트가 동일한 기본 데이터로 시작하도록 보장
     * 실행 시점: @Test 어노테이션이 붙은 각 메서드 실행 직전
     */
    @BeforeEach
    void setUp() {
        // 1단계: 테스트용 회원 생성 (댓글과 게시글의 작성자로 사용)
        testAuthor = Member.builder()
                .name("홍길동")                      // 회원 이름
                .email("test@example.com")          // 이메일 (로그인 ID로 사용)
                .password("password123")            // 비밀번호 (필수 필드)
                .phone("010-1234-5678")            // 연락처
                .address("서울시 강남구")            // 주소
                .memberType(MemberType.REGULAR)     // 회원 유형: 일반 회원
                .status(MemberStatus.ACTIVE)        // 회원 상태: 활성
                .role(Role.USER)                    // 권한: 일반 사용자
                .build();
        memberRepository.save(testAuthor);          // DB에 회원 저장

        // 2단계: 테스트용 게시글 생성 (댓글이 달릴 대상)
        testBoard = Board.builder()
                .title("테스트 게시글")              // 게시글 제목
                .content("테스트 내용입니다.")        // 게시글 내용
                .author(testAuthor)                 // 작성자 (위에서 생성한 회원 연결)
                .viewCount(0L)                      // 조회수 초기값
                .build();
        boardRepository.save(testBoard);            // DB에 게시글 저장
    }

    /**
     * findByBoardIdAndStatus 메서드 테스트
     * 
     * 테스트 목적: 특정 게시글의 활성 상태 댓글만 정확히 조회되는지 검증
     * 
     * 검증 사항:
     * 1. ACTIVE 상태의 댓글만 조회되는가?
     * 2. DELETED 상태의 댓글은 조회되지 않는가?
     * 3. 조회된 댓글의 내용과 상태가 올바른가?
     * 
     * Given-When-Then 패턴 사용:
     * - Given: 테스트에 필요한 데이터 준비
     * - When: 테스트 대상 메서드 실행
     * - Then: 결과 검증
     */
    @Test
    void findByBoardIdAndStatus_활성댓글조회() {
        // Given: 테스트 데이터 준비 - 활성 댓글 1개, 삭제된 댓글 1개 생성
        
        // 활성 상태의 댓글 생성
        Comment activeComment = Comment.builder()
                .content("첫 번째 댓글")             // 댓글 내용
                .board(testBoard)                   // 어떤 게시글에 달린 댓글인지
                .author(testAuthor)                 // 댓글 작성자
                .status(CommentStatus.ACTIVE)       // 상태: 활성 (화면에 표시됨)
                .build();
        commentRepository.save(activeComment);      // DB에 저장

        // 삭제된 상태의 댓글 생성 (조회되면 안 되는 데이터)
        Comment deletedComment = Comment.builder()
                .content("두 번째 댓글")             // 댓글 내용
                .board(testBoard)                   // 같은 게시글에 달린 댓글
                .author(testAuthor)                 // 같은 작성자
                .status(CommentStatus.DELETED)      // 상태: 삭제됨 (화면에 표시 안 됨)
                .build();
        commentRepository.save(deletedComment);     // DB에 저장

        // When: 실제 테스트 - 특정 게시글의 활성 댓글만 조회
        List<Comment> activeComments = commentRepository.findByBoardIdAndStatus(
                testBoard.getId(),                  // 조회할 게시글 ID
                CommentStatus.ACTIVE                // 조회할 댓글 상태 (활성만)
        );

        // Then: 결과 검증 - 활성 댓글 1개만 조회되고, 내용이 올바른지 확인
        assertThat(activeComments).hasSize(1);                              // 조회된 댓글 개수가 1개인지
        assertThat(activeComments.get(0).getContent()).isEqualTo("첫 번째 댓글");  // 댓글 내용이 맞는지
        assertThat(activeComments.get(0).getStatus()).isEqualTo(CommentStatus.ACTIVE);  // 상태가 ACTIVE인지
    }

    /**
     * countByBoardIdAndStatus 메서드 테스트
     * 
     * 테스트 목적: 특정 게시글의 활성 상태 댓글 개수를 정확히 계산하는지 검증
     * 
     * 검증 사항:
     * 1. ACTIVE 상태의 댓글만 카운트되는가?
     * 2. DELETED 상태의 댓글은 카운트에서 제외되는가?
     * 3. 반환되는 개수가 정확한가?
     * 
     * 실무 활용:
     * - 게시글 목록에서 "댓글 수" 표시
     * - 댓글이 있는 게시글 필터링
     */
    @Test
    void countByBoardIdAndStatus_댓글개수조회() {
        // Given: 테스트 데이터 준비 - 활성 댓글 3개, 삭제된 댓글 1개 생성
        
        // 반복문으로 활성 댓글 3개 생성 (실제 여러 사용자가 댓글을 단 상황 시뮬레이션)
        for (int i = 1; i <= 3; i++) {
            Comment comment = Comment.builder()
                    .content("댓글 " + i)            // 댓글 내용 (댓글 1, 댓글 2, 댓글 3)
                    .board(testBoard)               // 같은 게시글에 달린 댓글들
                    .author(testAuthor)             // 같은 작성자 (실제로는 다른 사용자일 수도)
                    .status(CommentStatus.ACTIVE)   // 모두 활성 상태
                    .build();
            commentRepository.save(comment);        // DB에 저장
        }

        // 삭제된 댓글 1개 추가 (카운트에서 제외되어야 함)
        Comment deletedComment = Comment.builder()
                .content("삭제된 댓글")              // 삭제된 댓글 내용
                .board(testBoard)                   // 같은 게시글
                .author(testAuthor)                 // 같은 작성자
                .status(CommentStatus.DELETED)      // 상태: 삭제됨
                .build();
        commentRepository.save(deletedComment);     // DB에 저장

        // When: 실제 테스트 - 특정 게시글의 활성 댓글 개수 조회
        Long activeCount = commentRepository.countByBoardIdAndStatus(
                testBoard.getId(),                  // 조회할 게시글 ID
                CommentStatus.ACTIVE                // 카운트할 댓글 상태 (활성만)
        );

        // Then: 결과 검증 - 활성 댓글 3개만 카운트되는지 확인 (삭제된 댓글 1개는 제외)
        assertThat(activeCount).isEqualTo(3L);      // 개수가 정확히 3개인지 (Long 타입이므로 3L)
    }
}
