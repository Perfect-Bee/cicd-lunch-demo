package com.camp.cicdlunchdemo.domain.service.recommendation;

import com.camp.cicdlunchdemo.domain.entity.LunchMenu;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 추천 알고리즘 전략 인터페이스
 */
public interface RecommendationStrategy {

    /**
     * 메뉴 목록에서 하나를 추천
     * @param menus 전체 메뉴 목록
     * @param excludeIds 제외할 메뉴 ID 목록
     * @return 추천된 메뉴
     */
    Optional<LunchMenu> recommend(List<LunchMenu> menus, Set<Long> excludeIds);

    /**
     * 전략 이름
     */
    String getStrategyName();
}
