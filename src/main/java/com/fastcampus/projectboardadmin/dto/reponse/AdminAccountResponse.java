package com.fastcampus.projectboardadmin.dto.reponse;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.AdminAccountDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public record AdminAccountResponse(
        String userId
        , String roleTypes
        , String email
        , String nickname
        , String memo
        , LocalDateTime createAt
        , String createBy
) {
    public  static AdminAccountResponse of(String userId, String roleTypes, String email, String nickname, String memo, LocalDateTime createAt, String createBy) {
        return new AdminAccountResponse(userId, roleTypes, email, nickname, memo, createAt, createBy);
    }

    public static AdminAccountResponse from(AdminAccountDto dto){
        return AdminAccountResponse.of(
                dto.userId()
                , dto.roleTypes().stream()
                        .map(RoleType::getDescription)
                        .collect(Collectors.joining(", "))
                , dto.email()
                , dto.nickname()
                , dto.memo()
                , dto.createAt()
                , dto.createBy()
        );
    }
}
