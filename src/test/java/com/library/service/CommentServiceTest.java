package com.library.service;


import com.library.dto.board.CommentCreateDTO;
import com.library.dto.board.CommentDTO;
import com.library.entity.board.Board;
import com.library.entity.board.Comment;
import com.library.entity.member.Member;
import com.library.repository.BoardRepository;
import com.library.repository.CommentRepository;
import com.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CommentService 단위 테스트
 * 
 * 테스트 전략:
 * - Mockito를 사용한 단위 테스트
 * - 실제 DB나 Spring 컨텍스트 없이 Service 로직만 테스트
 * - Repository를 Mock으로 대체하여 빠른 실행
 * 
 * 주요 테스트 항목:
 * 1. 댓글 생성 성공 케이스
 * 2. 게시글이 없을 때 예외 발생
 */
@ExtendWith(MockitoExtension.class)  // Mockito 사용을 위한 JUnit 5 확장
class CommentServiceTest {

    // Mock 객체: 실제 구현체 대신 가짜 객체 주입
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    // 테스트 대상: Mock 객체들이 주입된 실제 Service
    @InjectMocks
    private CommentService commentService;

    /**
     * 댓글 생성 정상 케이스 테스트
     * 
     * 검증 항목:
     * 1. 게시글과 작성자가 존재할 때 댓글이 정상 생성되는가?
     * 2. 생성된 댓글의 내용이 올바른가?
     * 3. Repository의 save 메서드가 정확히 1번 호출되는가?
     */
    @Test
    void createComment_정상작성() {
        // Given: 테스트 데이터 준비
        Long boardId = 1L;                          // 게시글 ID
        String loginId = "test@example.com";        // 로그인 사용자 이메일

        // 댓글 생성 요청 DTO
        CommentCreateDTO dto = CommentCreateDTO.builder()
                .content("테스트 댓글")               // 댓글 내용
                .build();

        // Mock 게시글 엔티티 생성
        Board board = Board.builder()
                .id(boardId)
                .build();
        
        // Mock 작성자 엔티티 생성
        Member author = Member.builder()
                .email(loginId)
                .name("홍길동")
                .build();
        
        // Mock Comment 엔티티 생성
        // 중요: CommentDTO.from()에서 사용하는 필드들을 모두 설정해야 함
        Comment comment = createCommentWithDates(
                1L,                                  // 댓글 ID
                "테스트 댓글",                        // 댓글 내용
                board,                               // 게시글
                author,                              // 작성자
                LocalDateTime.now(),                 // 생성일시
                LocalDateTime.now()                  // 수정일시
        );

        // Mock 동작 정의 (Stub)
        // boardRepository.findById()가 호출되면 board를 반환
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        
        // memberRepository.findByEmail()이 호출되면 author를 반환
        when(memberRepository.findByEmail(loginId)).thenReturn(Optional.of(author));
        
        // commentRepository.save()가 호출되면 comment를 반환
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When: 실제 테스트 대상 메서드 실행
        CommentDTO result = commentService.createComment(boardId, dto, loginId);

        // Then: 결과 검증
        // 반환된 DTO의 내용이 예상값과 일치하는지 확인
        assertThat(result.getContent()).isEqualTo("테스트 댓글");
        
        // Repository의 save 메서드가 정확히 1번 호출되었는지 검증
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    /**
     * 댓글 생성 실패 케이스 테스트 - 게시글이 존재하지 않음
     * 
     * 검증 항목:
     * 1. 존재하지 않는 게시글에 댓글 작성 시 예외가 발생하는가?
     * 2. 예외 메시지가 올바른가?
     */
    @Test
    void createComment_게시글없음_예외발생() {
        // Given: 존재하지 않는 게시글 ID
        Long boardId = 999L;                        // 존재하지 않는 게시글 ID
        
        CommentCreateDTO dto = CommentCreateDTO.builder()
                .content("테스트 댓글")
                .build();

        // Mock 동작 정의: 게시글을 찾지 못하도록 설정
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // When & Then: 예외 발생 검증
        // createComment 메서드 호출 시 IllegalArgumentException이 발생해야 함
        assertThatThrownBy(() ->
                commentService.createComment(boardId, dto, "test@example.com")
        )
                .isInstanceOf(IllegalArgumentException.class)      // 예외 타입 검증
                .hasMessage("게시글을 찾을 수 없습니다.");             // 예외 메시지 검증
    }

    /**
     * Comment 엔티티를 날짜 필드와 함께 생성하는 헬퍼 메서드
     * 
     * 이유:
     * - CommentDTO.from()은 createdAt, updatedAt 필드를 사용
     * - Mock으로 생성한 Comment는 이 필드들이 null
     * - 리플렉션으로 BaseEntity 필드를 설정해야 함
     * 
     * @param id 댓글 ID
     * @param content 댓글 내용
     * @param board 게시글
     * @param author 작성자
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @return 날짜 필드가 설정된 Comment 엔티티
     */
    private Comment createCommentWithDates(
            Long id,
            String content,
            Board board,
            Member author,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        
        // Builder로 Comment 기본 속성 설정
        Comment comment = Comment.builder()
                .id(id)
                .content(content)
                .board(board)
                .author(author)
                .build();
        
        // 리플렉션으로 BaseEntity의 날짜 필드 설정
        setBaseEntityFields(comment, createdAt, updatedAt);
        
        return comment;
    }

    /**
     * 리플렉션을 사용하여 BaseEntity의 날짜 필드 설정
     * 
     * 동작:
     * 1. Comment의 부모 클래스(BaseEntity)에서 필드 찾기
     * 2. setAccessible(true)로 접근 권한 획득
     * 3. 필드에 값 설정
     * 
     * @param comment 날짜를 설정할 Comment 엔티티
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     */
    private void setBaseEntityFields(
            Comment comment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        try {
            // createdAt 필드 설정
            java.lang.reflect.Field createdAtField = 
                comment.getClass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);         // private 필드 접근 허용
            createdAtField.set(comment, createdAt);     // 값 설정
            
            // updatedAt 필드 설정
            java.lang.reflect.Field updatedAtField = 
                comment.getClass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);         // private 필드 접근 허용
            updatedAtField.set(comment, updatedAt);     // 값 설정
            
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("BaseEntity에 필드가 존재하지 않습니다: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("BaseEntity 필드에 접근할 수 없습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("BaseEntity 필드 설정 중 오류 발생", e);
        }
    }
}
