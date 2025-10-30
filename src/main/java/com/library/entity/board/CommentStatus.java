package com.library.entity.board;

/*
    댓글 상태 Enum
        - ACTIVE: 활성 상태 (정상적으로 보임)
        - DELETED: 삭제된 상태 (소프트 삭제)
 */
public enum CommentStatus {
    ACTIVE,     // 활성
    DELETED     // 삭제됨
}
