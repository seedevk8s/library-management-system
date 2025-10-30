package com.library.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    좋아요 응답 DTO
        - 좋아요 토글 API의 응답 데이터
        - 클라이언트가 UI를 업데이트하는데 필요한 정보 포함
        
    포함 정보:
        - liked: 현재 사용자의 좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름)
        - likeCount: 해당 게시글의 총 좋아요 수
        
    사용 예시:
        {
            "liked": true,
            "likeCount": 15
        }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {
    
    /*
        현재 사용자의 좋아요 상태
            - true: 좋아요를 누른 상태
            - false: 좋아요를 누르지 않은 상태
            
        JavaScript에서 사용:
            if (response.liked) {
                // 하트를 빨간색으로 표시
            } else {
                // 하트를 회색으로 표시
            }
     */
    private boolean liked;
    
    /*
        게시글의 총 좋아요 수
            - 모든 사용자가 누른 좋아요의 합계
            
        JavaScript에서 사용:
            document.getElementById('likeCount').textContent = response.likeCount;
     */
    private Long likeCount;
}
