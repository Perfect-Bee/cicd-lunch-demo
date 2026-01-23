package com.camp.cicdlunchdemo.domain.service.recommendation;

import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 단순 랜덤 추천 전략
 * 모든 메뉴가 동일한 확률로 선택됨
 */
@Component
public class SimpleRandomStrategy extends AbstractRecommendationStrategy {

    @Override
    protected Optional<LunchMenu> selectMenu(List<LunchMenu> candidates) {
        int randomIndex = ThreadLocalRandom.current().nextInt(candidates.size());
        return Optional.of(candidates.get(randomIndex));
    }

    @Override
    public String getStrategyName() {
        return "SIMPLE_RANDOM";
    }
}
