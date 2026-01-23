package com.camp.cicdlunchdemo.domain.service.recommendation;

import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 가중치 기반 랜덤 추천 전략
 * weight 값이 높을수록 선택 확률이 높아짐
 */
@Component
public class WeightedRandomStrategy extends AbstractRecommendationStrategy {

    @Override
    protected Optional<LunchMenu> selectMenu(List<LunchMenu> candidates) {
        int totalWeight = candidates.stream()
                .mapToInt(LunchMenu::getWeight)
                .sum();

        if (totalWeight == 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(candidates.size());
            return Optional.of(candidates.get(randomIndex));
        }

        int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (LunchMenu menu : candidates) {
            cumulativeWeight += menu.getWeight();
            if (randomValue < cumulativeWeight) {
                return Optional.of(menu);
            }
        }

        return Optional.of(candidates.get(0));
    }

    @Override
    public String getStrategyName() {
        return "WEIGHTED_RANDOM";
    }
}
