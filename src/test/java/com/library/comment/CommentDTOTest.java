package com.library.comment;

import com.library.dto.board.CommentDTO;
import com.library.entity.board.Comment;
import com.library.entity.member.Member;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CommentDTO 변환 메서드 테스트
 * 
 * 목적: Entity → DTO 변환 로직이 올바르게 동작하는지 검증
 * 
 * 개선 사항:
 * - CommentTestBuilder 사용으로 테스트 코드 간결화
 * - 복잡한 리플렉션 로직을 헬퍼 클래스로 분리
 * - 테스트 가독성과 유지보수성 향상
 * 
 * 테스트 전략:
 * - @SpringBootTest 없이 순수 단위 테스트로 빠른 실행
 * - 테스트 Builder 패턴으로 재사용성 증가
 */
public class CommentDTOTest {

    /**
     * from 메서드 테스트: Entity를 DTO로 변환
     * 
     * 검증 항목:
     * 1. Comment의 기본 정보가 DTO로 정확히 변환되는가?
     * 2. 작성자(Member) 정보가 올바르게 추출되는가?
     * 3. 날짜 정보가 포맷팅되어 문자열로 변환되는가?
     * 
     * Given-When-Then 패턴:
     * - Given: 테스트용 Comment 엔티티 생성
     * - When: CommentDTO.from() 메서드로 변환
     * - Then: 변환된 DTO의 각 필드값 검증
     */
    @Test
    void from_Entity를DTO로변환() {
        // Given: 테스트용 Entity 준비
        
        // 작성자 Member 엔티티 생성
        // 댓글에는 작성자 정보가 필수이므로 먼저 생성
        Member author = Member.builder()
                .name("홍길동")                      // 작성자 이름
                .email("test@example.com")          // 작성자 이메일
                .build();

        // CommentTestBuilder를 사용하여 Comment 엔티티 생성
        // 장점: 복잡한 리플렉션 로직이 헬퍼 클래스로 숨겨짐
        //       테스트 코드가 비즈니스 로직에만 집중 가능
        Comment comment = CommentTestBuilder.createCommentWithId(
                1L,                                 // 댓글 ID
                "테스트 댓글",                       // 댓글 내용
                author                              // 작성자
        );
        // 참고: createCommentWithId는 내부적으로 createdAt, updatedAt도 자동 설정

        // When: Entity → DTO 변환
        // CommentDTO의 정적 팩토리 메서드 from() 호출
        CommentDTO dto = CommentDTO.from(comment);

        // Then: 변환 결과 검증
        // AssertJ의 assertThat을 사용하여 각 필드를 검증
        
        assertThat(dto.getId()).isEqualTo(1L);                          
        // ID가 정확히 변환되었는지 확인
        
        assertThat(dto.getContent()).isEqualTo("테스트 댓글");            
        // 댓글 내용이 그대로 전달되었는지 확인
        
        assertThat(dto.getAuthorName()).isEqualTo("홍길동");             
        // Member 엔티티에서 작성자 이름이 제대로 추출되었는지 확인
        
        assertThat(dto.getAuthorEmail()).isEqualTo("test@example.com"); 
        // Member 엔티티에서 작성자 이메일이 제대로 추출되었는지 확인
        
        assertThat(dto.getCreatedAt()).isNotNull();                     
        // 생성일시가 포맷팅되어 null이 아닌 문자열로 변환되었는지 확인
        // 예상 포맷: "2025-10-21 13:30"
        
        assertThat(dto.getUpdatedAt()).isNotNull();                     
        // 수정일시가 포맷팅되어 null이 아닌 문자열로 변환되었는지 확인
        
        // 추가 검증: 날짜 포맷이 올바른지 확인
        assertThat(dto.getCreatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
        // 정규표현식으로 "yyyy-MM-dd HH:mm" 포맷 검증
        // 예: "2025-10-21 13:30" 형태인지 확인
        
        assertThat(dto.getUpdatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
        // 동일하게 updatedAt도 포맷 검증
    }

    /**
     * from 메서드 테스트: 다양한 작성자 정보 검증
     * 
     * 목적: 서로 다른 작성자 정보가 올바르게 변환되는지 확인
     * 
     * 실무 시나리오:
     * - 여러 사용자가 작성한 댓글들을 DTO로 변환할 때
     * - 각 댓글의 작성자 정보가 정확히 구분되어야 함
     */
    @Test
    void from_다양한작성자정보변환() {
        // Given: 다른 작성자 정보로 댓글 생성
        Member anotherAuthor = Member.builder()
                .name("김철수")                      // 다른 이름
                .email("kim@example.com")           // 다른 이메일
                .build();

        Comment comment = CommentTestBuilder.createCommentWithId(
                2L,
                "또 다른 댓글입니다",
                anotherAuthor
        );

        // When: DTO 변환
        CommentDTO dto = CommentDTO.from(comment);

        // Then: 작성자 정보가 정확히 변환되었는지 검증
        assertThat(dto.getAuthorName()).isEqualTo("김철수");
        assertThat(dto.getAuthorEmail()).isEqualTo("kim@example.com");
        
        // 다른 댓글과 혼동되지 않도록 ID도 확인
        assertThat(dto.getId()).isEqualTo(2L);
    }
}

