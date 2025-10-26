package com.library.service;

import com.library.dto.board.LikeResponseDTO;
import com.library.entity.board.Board;
import com.library.entity.board.BoardLike;
import com.library.entity.member.Member;
import com.library.repository.BoardLikeRepository;
import com.library.repository.BoardRepository;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    게시글 좋아요 Service
        - 좋아요 비즈니스 로직 처리
        - 좋아요 토글 (누르기/취소)
        - 좋아요 상태 확인
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션
public class BoardLikeService {
    
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    
    /*
        좋아요 토글 (누르기/취소)
            - 이미 좋아요를 눌렀으면 → 취소 (BoardLike 삭제)
            - 좋아요를 누르지 않았으면 → 누르기 (BoardLike 생성)
            - Board의 likeCount를 함께 업데이트
            
        @Transactional: 데이터 변경 작업이므로 쓰기 가능 트랜잭션 필요
            - BoardLike 생성/삭제와 likeCount 업데이트가 원자적으로 처리됨
            - 중간에 예외 발생 시 모든 변경사항 롤백
            
        파라미터:
            - boardId: 게시글 ID
            - userEmail: 현재 로그인한 사용자의 이메일 (Spring Security에서 제공)
            
        반환값:
            - LikeResponseDTO: 좋아요 상태와 좋아요 수
     */
    @Transactional  // 쓰기 가능 트랜잭션
    public LikeResponseDTO toggleLike(Long boardId, String userEmail) {
        log.info("좋아요 토글 요청 - 게시글 ID: {}, 사용자: {}", boardId, userEmail);
        
        // 1. 게시글 조회
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> {
                log.error("게시글을 찾을 수 없음 - ID: {}", boardId);
                return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
            });
        
        // 2. 사용자 조회
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> {
                log.error("사용자를 찾을 수 없음 - 이메일: {}", userEmail);
                return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            });
        
        // 3. 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = boardLikeRepository.existsByBoardIdAndMemberId(
            boardId, 
            member.getId()
        );
        
        boolean liked;  // 최종 좋아요 상태
        
        if (alreadyLiked) {
            // 3-1. 이미 좋아요를 눌렀으면 → 취소 (삭제)
            log.info("좋아요 취소 - 게시글 ID: {}, 사용자 ID: {}", boardId, member.getId());
            
            BoardLike boardLike = boardLikeRepository.findByBoardIdAndMemberId(
                boardId, 
                member.getId()
            ).orElseThrow(() -> new IllegalStateException("좋아요 정보를 찾을 수 없습니다."));
            
            boardLikeRepository.delete(boardLike);  // BoardLike 삭제
            board.decreaseLikeCount();  // likeCount 감소
            liked = false;
            
            log.info("좋아요 취소 완료 - 현재 좋아요 수: {}", board.getLikeCount());
            
        } else {
            // 3-2. 좋아요를 누르지 않았으면 → 누르기 (생성)
            log.info("좋아요 추가 - 게시글 ID: {}, 사용자 ID: {}", boardId, member.getId());
            
            BoardLike boardLike = BoardLike.builder()
                .board(board)
                .member(member)
                .build();
            
            boardLikeRepository.save(boardLike);  // BoardLike 저장
            board.increaseLikeCount();  // likeCount 증가
            liked = true;
            
            log.info("좋아요 추가 완료 - 현재 좋아요 수: {}", board.getLikeCount());
        }
        
        // 4. 응답 DTO 생성 및 반환
        return LikeResponseDTO.builder()
            .liked(liked)
            .likeCount(board.getLikeCount())
            .build();
    }
    
    /*
        좋아요 상태 확인
            - 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
            - 페이지 로드 시 하트 버튼의 초기 상태를 설정하는데 사용
            
        파라미터:
            - boardId: 게시글 ID
            - userEmail: 현재 로그인한 사용자의 이메일
            
        반환값:
            - LikeResponseDTO: 좋아요 상태와 좋아요 수
     */
    public LikeResponseDTO getLikeStatus(Long boardId, String userEmail) {
        log.info("좋아요 상태 조회 - 게시글 ID: {}, 사용자: {}", boardId, userEmail);
        
        // 1. 게시글 조회
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 2. 로그인하지 않은 경우 (userEmail이 null)
        if (userEmail == null) {
            log.info("비로그인 사용자 - 좋아요 상태: false");
            return LikeResponseDTO.builder()
                .liked(false)
                .likeCount(board.getLikeCount())
                .build();
        }
        
        // 3. 사용자 조회
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 4. 좋아요 여부 확인
        boolean liked = boardLikeRepository.existsByBoardIdAndMemberId(
            boardId, 
            member.getId()
        );
        
        log.info("좋아요 상태 조회 완료 - 좋아요 여부: {}, 좋아요 수: {}", liked, board.getLikeCount());
        
        // 5. 응답 DTO 생성 및 반환
        return LikeResponseDTO.builder()
            .liked(liked)
            .likeCount(board.getLikeCount())
            .build();
    }
}
