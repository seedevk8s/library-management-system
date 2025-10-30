package com.library.exception;

import com.library.dto.common.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

/*
       전역 예외 처리 핸들러 - 애플리케이션 전체에서 발생하는 예외를 중앙에서 처리
       @ControllerAdvice
            - Spring의 AOP를 활용한 전역 예외 처리 메커니즘
                - 모든 @Controller에서 발생하는 예외를 한 곳에서 처리
                - 코드 중복 제거 및 일관된 에러 응답 제공
                - Controller에서 try~catch 불필요

      예외 처리 우선순위 (구체적인 것 => 일반적인 것)
        1) InvalidFileException (파일 검증 실패)
        2) MaxUploadSizeExceededException (Spring 파일 크기 제한)
        3) MethodArgumentNotValidException (Bean Validation 실패)
        4) IllegalArgumentException (비즈니스 로직 검증 실패)
        5) RuntimeException (일반 런타임 에러)
        6) Exception (모든 예외의 최종 방어선)
        
      REST API 예외는 JSON으로, 일반 요청 예외는 HTML로 응답
 */
@ControllerAdvice   // 모든 Controller에 적용되는 전역 예외 처리
@Slf4j
public class GlobalExceptionHandler {

    /*
        REST API 전용 예외 처리
            일반 HTML 응답과의 차이
            코드를 보면 아래쪽에 ModelAndView를 반환하는 핸들러들도 있는데, 이들은:

            일반 웹 페이지 요청 → **에러 페이지(HTML)**로 표시
            REST API 요청 → JSON 응답으로 표시

            따라서 이 두 핸들러는 JavaScript(fetch, axios 등)로 호출하는 REST API의 에러를
            JSON 형태로 프론트엔드에 전달하는 역할을 합니다!
     */
    
    // Bean Validation 실패 (REST API)
    @ExceptionHandler(MethodArgumentNotValidException.class)  // @Valid 검증 실패 시 발생하는 예외 처리
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
        log.error("유효성 검증 실패: {}", e.getMessage());  // 에러 로그 기록
        
        // 모든 필드 에러 메시지를 하나로 합침
        String errorMessage = e.getBindingResult().getFieldErrors().stream()  // 검증 실패한 모든 필드 에러를 Stream으로 변환
                .map(FieldError::getDefaultMessage)  // 각 필드 에러에서 기본 에러 메시지 추출
                .collect(Collectors.joining(", "));  // 모든 에러 메시지를 쉼표로 구분하여 하나의 문자열로 결합
        
        return ResponseEntity  // ResponseEntity 빌더 시작
                .status(HttpStatus.BAD_REQUEST)  // HTTP 400 Bad Request 상태코드 설정
                .body(new ErrorResponseDTO(errorMessage));  // 결합된 에러 메시지를 담은 ErrorResponseDTO를 응답 본문으로 설정
    }
    
    // 비즈니스 로직 검증 실패 (REST API)
    @ExceptionHandler(IllegalArgumentException.class)  // 비즈니스 로직 검증 실패 시 발생하는 예외 처리
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("비즈니스 로직 검증 실패: {}", e.getMessage());  // 에러 로그 기록
        
        return ResponseEntity  // ResponseEntity 빌더 시작
                .status(HttpStatus.BAD_REQUEST)  // HTTP 400 Bad Request 상태코드 설정
                .body(new ErrorResponseDTO(e.getMessage()));  // 예외 메시지를 담은 ErrorResponseDTO를 응답 본문으로 설정
    }

    /*
        일반 HTML 응답 예외 처리
     */

    @ExceptionHandler(InvalidFileException.class)
    public ModelAndView handleInvalidFileException(InvalidFileException e) {
        log.error("파일 검증 실패: {}", e.getMessage());

        ModelAndView mav = new ModelAndView("error/file-error");
        mav.addObject("errorTitle", "파일 업로드 실패");
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("errorDetail", "다시 시도해주세요. 문제가 계속되면 관리자에게 문의하세요.");
        mav.setStatus(HttpStatus.BAD_REQUEST);

        return mav;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("파일 크기 초과: {}", e.getMessage());

        ModelAndView mav = new ModelAndView("error/file-error");
        mav.addObject("errorTitle", "파일 크기 초과");
        mav.addObject("errorMessage", "업로드 가능한 최대 파일 크기는 10MB입니다.");
        mav.addObject("errorDetail", "더 작은 파일을 선택하거나 파일을 압축해주세요.");
        mav.setStatus(HttpStatus.BAD_REQUEST);

        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException e) {
        log.error("런타입 예외 발생: {}", e.getMessage());

        ModelAndView mav = new ModelAndView("error/file-error");
        mav.addObject("errorTitle", "오류 발생");
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("errorDetail", "요청을 처리하는 중 문제가 발생했습니다.");
        mav.setStatus(HttpStatus.BAD_REQUEST);

        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handlerException(Exception e) {
        log.error("예외 발생: {}", e.getMessage(), e);

        ModelAndView mav = new ModelAndView("error/file-error");
        mav.addObject("errorTitle", "시스템 발생");
        mav.addObject("errorMessage", "일시적인 오류가 발생했습니다.");
        mav.addObject("errorDetail", "잠시 후 다시 시도해주세요.");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return mav;

    }
}
