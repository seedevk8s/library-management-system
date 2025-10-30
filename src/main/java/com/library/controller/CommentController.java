package com.library.controller;

import com.library.dto.board.CommentCreateDTO;
import com.library.dto.board.CommentDTO;
import com.library.dto.board.CommentUpdateDTO;
import com.library.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    댓글 Controller
        - 댓글 관련 REST API 제공
        - JSON 형식으로 요청/응답 처리
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    
    private final CommentService commentService;
    
    /*
        게시글별 댓글 목록 조회 API
            - GET /api/comments/boards/{boardId}
            - 특정 게시글의 모든 활성 댓글 조회
     */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBoardId(
            @PathVariable Long boardId
    ) {
        log.info("댓글 목록 조회 요청 - 게시글 ID: {}", boardId);
        
        List<CommentDTO> comments = commentService.getCommentsByBoardId(boardId);
        
        log.info("댓글 목록 조회 완료 - 댓글 수: {}", comments.size());
        return ResponseEntity.ok(comments);  // HTTP 200 OK 상태코드와 댓글 목록을 응답
    }
    
    /*
        댓글 작성 API
            - POST /api/comments/boards/{boardId}
            - 특정 게시글에 새 댓글 작성
     */
    @PostMapping("/boards/{boardId}")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long boardId,  // URL 경로에서 게시글 ID 추출 (예: /api/comments/boards/123 → boardId = 123)
            @Valid @RequestBody CommentCreateDTO dto,  // 요청 본문(JSON)을 DTO로 변환 후 유효성 검증 (@Valid)
            @AuthenticationPrincipal UserDetails userDetails  // Spring Security에서 현재 로그인한 사용자 정보 주입
    ) {
        log.info("댓글 작성 요청 - 게시글 ID: {}, 작성자: {}", boardId, userDetails.getUsername());

        String userEmail = userDetails.getUsername(); // userDetails.getUsername()은 Member의 email을 반환
        
        CommentDTO comment = commentService.createComment(boardId, dto, userEmail);
        
        log.info("댓글 작성 완료 - 댓글 ID: {}", comment.getId());
        
        /*
            HTTP 201 Created 상태코드와 생성된 댓글 데이터를 응답
                - ResponseEntity.status(HttpStatus.CREATED): HTTP 201 Created 상태코드 설정
                    * 201 Created: 새로운 리소스(댓글)가 성공적으로 생성되었음을 의미
                    * POST 요청의 성공적인 리소스 생성 시 표준 응답 코드
                - .body(comment): 생성된 댓글 정보를 응답 본문(body)에 포함
                    * CommentDTO 객체가 자동으로 JSON 형식으로 변환되어 클라이언트에 전송
                    * 클라이언트는 생성된 댓글의 ID, 내용, 작성자, 작성시간 등을 즉시 확인 가능
         */
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    
    /*
        댓글 수정 API
            - PUT /api/comments/{commentId}
            - 기존 댓글 내용 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,  // URL 경로에서 댓글 ID 추출 (예: /api/comments/456 → commentId = 456)
            @Valid @RequestBody CommentUpdateDTO dto,  // 요청 본문(JSON)을 DTO로 변환 후 유효성 검증 (@Valid)
            @AuthenticationPrincipal UserDetails userDetails  // Spring Security에서 현재 로그인한 사용자 정보 주입
    ) {
        log.info("댓글 수정 요청 - 댓글 ID: {}, 수정자: {}", commentId, userDetails.getUsername());
        
        String userEmail = userDetails.getUsername();
        
        CommentDTO comment = commentService.updateComment(commentId, dto, userEmail);
        
        log.info("댓글 수정 완료 - 댓글 ID: {}", commentId);
        return ResponseEntity.ok(comment);  // HTTP 200 OK 상태코드와 수정된 댓글 데이터를 응답
    }
    
    /*
        댓글 삭제 API
            - DELETE /api/comments/{commentId}
            - 댓글 소프트 삭제 (status를 DELETED로 변경)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("댓글 삭제 요청 - 댓글 ID: {}, 삭제자: {}", commentId, userDetails.getUsername());
        
        String userEmail = userDetails.getUsername();
        
        commentService.deleteComment(commentId, userEmail);
        
        log.info("댓글 삭제 완료 - 댓글 ID: {}", commentId);
        return ResponseEntity.noContent().build();
    }  // HTTP 204 No Content 상태코드로 응답 (본문 없이 삭제 성공만 전달)
}
