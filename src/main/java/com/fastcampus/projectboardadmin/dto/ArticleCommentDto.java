package com.fastcampus.projectboardadmin.dto;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id
        , Long articleId
        , UserAccountDto userAccount
        , Long parentCommentId
        , String content
        , LocalDateTime createAt
        , String createBy
        , LocalDateTime modifiedAt
        , String modifiedBy
) {
    public static ArticleCommentDto of(Long id, Long articleId, UserAccountDto userAccount, Long parentCommentId, String content, LocalDateTime createAt, String createBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userAccount, parentCommentId, content, createAt, createBy, modifiedAt, modifiedBy);
    }
}
