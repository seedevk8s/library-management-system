package com.library.controller;

import com.library.dto.board.LikeResponseDTO;
import com.library.service.BoardLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/*
    게시글 좋아요 Controller
        - 좋아요 관련 REST API 제공
        - JSON 형식으로 요청/응답 처리
 */
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardLikeController {
    
    private final BoardLikeService boardLikeService;
    
    /*
        좋아요 토글 API (누르기/취소)
            - POST /api/boards/{boardId}/like
            - 이미 좋아요를 눌렀으면 취소, 누르지 않았으면 누르기
            - 로그인한 사용자만 사용 가능 (SecurityConfig에서 인증 필요로 설정)
            
        요청:
            - Method: POST
            - URL: /api/boards/{boardId}/like
            - Header: CSRF 토큰 포함
            - Body: 없음
            
        응답:
            - HTTP 200 OK
            - Body: {
                "liked": true,      // 현재 좋아요 상태
                "likeCount": 15     // 총 좋아요 수
            }
            
        예외:
            - 401 Unauthorized: 로그인하지 않은 경우
            - 404 Not Found: 게시글을 찾을 수 없는 경우
     */
    @PostMapping("/{boardId}/like")
    public ResponseEntity<LikeResponseDTO> toggleLike(
            @PathVariable Long boardId,  // URL 경로에서 게시글 ID 추출
            @AuthenticationPrincipal UserDetails userDetails  // Spring Security에서 현재 로그인한 사용자 정보 주입
    ) {
        log.info("좋아요 토글 API 요청 - 게시글 ID: {}, 사용자: {}", boardId, userDetails.getUsername());
        
        // userDetails.getUsername()은 Member의 email을 반환
        String userEmail = userDetails.getUsername();
        
        // 좋아요 토글 서비스 호출
        LikeResponseDTO response = boardLikeService.toggleLike(boardId, userEmail);
        
        log.info("좋아요 토글 완료 - 좋아요 상태: {}, 좋아요 수: {}", 
                response.isLiked(), response.getLikeCount());
        
        /*
            HTTP 200 OK 상태코드와 좋아요 응답 데이터를 반환
                - ResponseEntity.ok(): HTTP 200 OK 상태코드 설정
                - body(response): LikeResponseDTO 객체가 자동으로 JSON 형식으로 변환되어 전송
                - 클라이언트는 응답을 받아 UI를 업데이트 (하트 색상, 좋아요 수)
         */
        return ResponseEntity.ok(response);
    }
    
    /*
        좋아요 상태 조회 API
            - GET /api/boards/{boardId}/like
            - 현재 사용자의 좋아요 상태와 총 좋아요 수 조회
            - 로그인 여부와 관계없이 사용 가능
            
        요청:
            - Method: GET
            - URL: /api/boards/{boardId}/like
            - Header: 없음 (로그인 시 자동으로 인증 정보 포함)
            
        응답:
            - HTTP 200 OK
            - Body: {
                "liked": false,     // 현재 좋아요 상태 (비로그인 시 항상 false)
                "likeCount": 15     // 총 좋아요 수
            }
            
        사용 시나리오:
            - 페이지 로드 시 하트 버튼의 초기 상태 설정
            - 로그인: liked가 true/false로 하트 색상 결정
            - 비로그인: liked가 false로 회색 하트 표시
     */
    @GetMapping("/{boardId}/like")
    public ResponseEntity<LikeResponseDTO> getLikeStatus(
            @PathVariable Long boardId,  // URL 경로에서 게시글 ID 추출
            @AuthenticationPrincipal UserDetails userDetails  // 로그인한 경우에만 값이 들어옴 (비로그인 시 null)
    ) {
        // 로그인 여부 확인
        String userEmail = (userDetails != null) ? userDetails.getUsername() : null;
        
        log.info("좋아요 상태 조회 API 요청 - 게시글 ID: {}, 사용자: {}", 
                boardId, (userEmail != null ? userEmail : "비로그인"));
        
        // 좋아요 상태 조회 서비스 호출
        LikeResponseDTO response = boardLikeService.getLikeStatus(boardId, userEmail);
        
        log.info("좋아요 상태 조회 완료 - 좋아요 상태: {}, 좋아요 수: {}", 
                response.isLiked(), response.getLikeCount());
        
        // HTTP 200 OK 상태코드와 좋아요 응답 데이터를 반환
        return ResponseEntity.ok(response);
    }
}
