package com.camp.cicdlunchdemo.domain.service.recommendation;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WeightedRandomStrategy 단위 테스트")
class WeightedRandomStrategyTest {

    private WeightedRandomStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new WeightedRandomStrategy();
    }

    @Nested
    @DisplayName("recommend 메서드")
    class Recommend {

        @Test
        @DisplayName("빈 메뉴 목록이면 빈 Optional을 반환한다")
        void returnsEmptyWhenMenuListIsEmpty() {
            // given
            List<LunchMenu> emptyMenus = Collections.emptyList();

            // when
            Optional<LunchMenu> result = strategy.recommend(emptyMenus, Set.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("가중치가 높은 메뉴가 더 자주 선택된다")
        void higherWeightMenuIsSelectedMoreOften() {
            // given
            LunchMenu lowWeight = createMenu(1L, "낮은가중치", Category.KOREAN, 1);
            LunchMenu highWeight = createMenu(2L, "높은가중치", Category.KOREAN, 9);
            List<LunchMenu> menus = List.of(lowWeight, highWeight);

            // when
            Map<Long, Integer> selectionCount = new HashMap<>();
            int iterations = 1000;

            for (int i = 0; i < iterations; i++) {
                Optional<LunchMenu> result = strategy.recommend(menus, Set.of());
                result.ifPresent(menu -> 
                    selectionCount.merge(menu.getId(), 1, Integer::sum));
            }

            // then - 가중치 비율(1:9)에 가까운 선택 비율
            int lowWeightCount = selectionCount.getOrDefault(1L, 0);
            int highWeightCount = selectionCount.getOrDefault(2L, 0);

            assertThat(highWeightCount).isGreaterThan(lowWeightCount * 3);
        }

        @Test
        @DisplayName("모든 가중치가 0이면 균등 확률로 선택한다")
        void selectsUniformlyWhenAllWeightsAreZero() {
            // given
            List<LunchMenu> menus = List.of(
                    createMenu(1L, "메뉴1", Category.KOREAN, 0),
                    createMenu(2L, "메뉴2", Category.KOREAN, 0)
            );

            // when
            Optional<LunchMenu> result = strategy.recommend(menus, Set.of());

            // then
            assertThat(result).isPresent();
            assertThat(menus).contains(result.get());
        }

        @Test
        @DisplayName("제외 목록에 있는 메뉴는 선택하지 않는다")
        void excludesMenusInExcludeList() {
            // given
            List<LunchMenu> menus = createTestMenus();
            Set<Long> excludeIds = Set.of(1L);

            // when & then
            for (int i = 0; i < 50; i++) {
                Optional<LunchMenu> result = strategy.recommend(menus, excludeIds);
                assertThat(result).isPresent();
                assertThat(result.get().getId()).isNotEqualTo(1L);
            }
        }
    }

    @Test
    @DisplayName("전략 이름은 WEIGHTED_RANDOM이다")
    void strategyNameIsWeightedRandom() {
        assertThat(strategy.getStrategyName()).isEqualTo("WEIGHTED_RANDOM");
    }

    private List<LunchMenu> createTestMenus() {
        return List.of(
                createMenu(1L, "김치찌개", Category.KOREAN, 3),
                createMenu(2L, "짜장면", Category.CHINESE, 2),
                createMenu(3L, "초밥", Category.JAPANESE, 1)
        );
    }

    private LunchMenu createMenu(Long id, String name, Category category, int weight) {
        LunchMenu menu = LunchMenu.builder()
                .name(name)
                .category(category)
                .weight(weight)
                .build();
        try {
            var idField = LunchMenu.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(menu, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return menu;
    }
}
