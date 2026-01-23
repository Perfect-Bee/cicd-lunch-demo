package com.camp.cicdlunchdemo.domain.service.recommendation;

import com.camp.cicdlunchdemo.domain.entity.LunchMenu;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 추천 전략 추상 클래스
 * 공통 로직(필터링)을 제공하고, 실제 선택 로직은 하위 클래스에서 구현
 */
public abstract class AbstractRecommendationStrategy implements RecommendationStrategy {

    @Override
    public Optional<LunchMenu> recommend(List<LunchMenu> menus, Set<Long> excludeIds) {
        if (menus.isEmpty()) {
            return Optional.empty();
        }

        List<LunchMenu> candidates = filterCandidates(menus, excludeIds);

        if (candidates.isEmpty()) {
            candidates = menus;
        }

        return selectMenu(candidates);
    }

    /**
     * 제외 대상을 필터링한 후보 목록 반환
     */
    protected List<LunchMenu> filterCandidates(List<LunchMenu> menus, Set<Long> excludeIds) {
        if (excludeIds == null || excludeIds.isEmpty()) {
            return menus;
        }
        return menus.stream()
                .filter(menu -> !excludeIds.contains(menu.getId()))
                .toList();
    }

    /**
     * 후보 목록에서 메뉴 하나를 선택 (하위 클래스에서 구현)
     */
    protected abstract Optional<LunchMenu> selectMenu(List<LunchMenu> candidates);
}
