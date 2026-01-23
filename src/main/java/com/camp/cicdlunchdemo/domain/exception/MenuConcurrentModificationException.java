package com.camp.cicdlunchdemo.domain.exception;

public class MenuConcurrentModificationException extends RuntimeException {

    public MenuConcurrentModificationException() {
        super("다른 사용자가 이미 메뉴를 수정했습니다. 페이지를 새로고침 후 다시 시도해주세요.");
    }

    public MenuConcurrentModificationException(String message) {
        super(message);
    }
}
