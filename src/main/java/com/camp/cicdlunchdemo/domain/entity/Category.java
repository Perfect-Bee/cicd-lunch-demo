package com.camp.cicdlunchdemo.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    FASTFOOD("패스트푸드"),
    ETC("기타");

    private final String displayName;
}
