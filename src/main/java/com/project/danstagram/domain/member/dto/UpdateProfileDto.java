package com.project.danstagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileDto {

    private String memberWebsite;
    private String memberIntroduce;
    private String memberGender;
}
