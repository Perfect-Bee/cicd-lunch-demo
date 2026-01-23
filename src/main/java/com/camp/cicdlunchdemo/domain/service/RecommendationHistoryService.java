package com.camp.cicdlunchdemo.domain.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 추천 기록 관리 서비스
 * 세션 기반으로 최근 추천된 메뉴 ID를 관리 (슬라이딩 윈도우 방식)
 */
@Service
public class RecommendationHistoryService {

    private static final String SESSION_KEY = "recentRecommendedMenus";
    private static final int MAX_HISTORY_SIZE = 5;

    /**
     * 최근 추천된 메뉴 ID 목록 조회
     * @return 최근 추천 메뉴 ID Set (없으면 빈 Set)
     */
    @SuppressWarnings("unchecked")
    public Set<Long> getRecentMenuIds(HttpSession session) {
        Set<Long> history = (Set<Long>) session.getAttribute(SESSION_KEY);
        return history != null ? history : Set.of();
    }

    /**
     * 추천 기록에 메뉴 ID 추가
     * MAX_HISTORY_SIZE 초과 시 가장 오래된 항목 제거
     */
    public void addToHistory(HttpSession session, Long menuId) {
        Set<Long> history = getOrCreateHistory(session);

        // 이미 있으면 제거 후 다시 추가 (순서 갱신)
        history.remove(menuId);
        history.add(menuId);

        // 크기 초과 시 가장 오래된 항목 제거
        while (history.size() > MAX_HISTORY_SIZE) {
            Long oldest = history.iterator().next();
            history.remove(oldest);
        }

        session.setAttribute(SESSION_KEY, history);
    }

    /**
     * 추천 기록 초기화
     */
    public void clearHistory(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getOrCreateHistory(HttpSession session) {
        Set<Long> history = (Set<Long>) session.getAttribute(SESSION_KEY);
        if (history == null) {
            history = new LinkedHashSet<>();
            session.setAttribute(SESSION_KEY, history);
        }
        return history;
    }
}
