package com.library;

// 댓글 관련 Entity 클래스 임포트
import com.library.entity.board.Board;
import com.library.entity.board.Comment;
import com.library.entity.board.CommentStatus;
import com.library.entity.member.Member;
// JUnit 5 테스트 애노테이션
import org.junit.jupiter.api.Test;
// Spring Boot 통합 테스트를 위한 애노테이션
import org.springframework.boot.test.context.SpringBootTest;
// AssertJ 라이브러리의 검증 메서드
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Boot 애플리케이션 통합 테스트 클래스
 * @SpringBootTest: 전체 Spring Context를 로드하여 테스트 수행
 */
@SpringBootTest
class LibraryManagementSystemApplicationTests {

	/**
	 * Spring Context 로딩 테스트
	 * - 애플리케이션이 정상적으로 시작되는지 확인
	 * - 모든 Bean이 정상적으로 생성되는지 검증
	 */
	@Test
	void contextLoads() {
		// Spring Context가 정상적으로 로드되면 테스트 통과
	}

	/**
	 * Comment Entity 객체 생성 및 검증 테스트
	 * - 순수 자바 객체(POJO) 테스트
	 * - DB 접근 없이 메모리상에서만 객체 생성 및 검증
	 * - 테이블이 없어도 실행 가능
	 */
    @Test
    void commentEntityTest() {
        // Given: 테스트 데이터 준비
        // Board 객체 생성 (Builder 패턴 사용)
        Board board = Board.builder()
                .title("테스트 게시글")  // 게시글 제목
                .content("내용")         // 게시글 내용
                .build();

        // Member 객체 생성 (댓글 작성자)
        Member member = Member.builder()
                .name("홍길동")                  // 회원 이름
                .email("test@example.com")      // 회원 이메일
                .build();

        // When: 실제 테스트 수행
        // Comment 객체 생성 (Builder 패턴 사용)
        Comment comment = Comment.builder()
                .content("테스트 댓글")              // 댓글 내용
                .board(board)                       // 연관된 게시글
                .author(member)                     // 댓글 작성자
                .status(CommentStatus.ACTIVE)       // 댓글 상태 (활성)
                .build();

        // Then: 결과 검증
        // AssertJ를 사용한 검증 - 댓글 내용 확인
        assertThat(comment.getContent()).isEqualTo("테스트 댓글");
        // AssertJ를 사용한 검증 - 댓글 상태 확인
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
    }

}
