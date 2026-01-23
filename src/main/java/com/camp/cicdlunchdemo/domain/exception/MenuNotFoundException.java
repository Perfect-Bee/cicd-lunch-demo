package com.camp.cicdlunchdemo.domain.exception;

public class MenuNotFoundException extends RuntimeException {

    public MenuNotFoundException(Long id) {
        super("메뉴를 찾을 수 없습니다. ID: " + id);
    }

    public MenuNotFoundException(String message) {
        super(message);
    }
}
