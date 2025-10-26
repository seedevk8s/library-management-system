package com.library.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/*
    REST API 에러 응답 DTO
        - REST API 요청 실패 시 클라이언트에게 반환되는 에러 정보
        - JSON 형식으로 에러 메시지와 타임스탬프 전달
 */
@Getter
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;             // 에러 메시지
    private LocalDateTime timestamp;    // 에러 발생 시간
    
    // 단일 파라미터 생성자 (timestamp는 현재 시간으로 자동 설정)
    public ErrorResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
