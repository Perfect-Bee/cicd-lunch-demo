package com.camp.cicdlunchdemo.domain.service.recommendation;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleRandomStrategy 단위 테스트")
class SimpleRandomStrategyTest {

    private SimpleRandomStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SimpleRandomStrategy();
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
        @DisplayName("메뉴 목록에서 하나를 랜덤으로 선택한다")
        void selectsOneMenuFromList() {
            // given
            List<LunchMenu> menus = createTestMenus();

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
            Set<Long> excludeIds = Set.of(1L, 2L);

            // when & then
            for (int i = 0; i < 100; i++) {
                Optional<LunchMenu> result = strategy.recommend(menus, excludeIds);
                assertThat(result).isPresent();
                assertThat(result.get().getId()).isNotIn(1L, 2L);
            }
        }

        @Test
        @DisplayName("모든 메뉴가 제외되면 전체 목록에서 선택한다")
        void selectsFromAllWhenAllExcluded() {
            // given
            List<LunchMenu> menus = createTestMenus();
            Set<Long> excludeIds = Set.of(1L, 2L, 3L);

            // when
            Optional<LunchMenu> result = strategy.recommend(menus, excludeIds);

            // then
            assertThat(result).isPresent();
            assertThat(menus).contains(result.get());
        }

        @Test
        @DisplayName("제외 목록이 null이면 전체 목록에서 선택한다")
        void selectsFromAllWhenExcludeIdsIsNull() {
            // given
            List<LunchMenu> menus = createTestMenus();

            // when
            Optional<LunchMenu> result = strategy.recommend(menus, null);

            // then
            assertThat(result).isPresent();
            assertThat(menus).contains(result.get());
        }
    }

    @Test
    @DisplayName("전략 이름은 SIMPLE_RANDOM이다")
    void strategyNameIsSimpleRandom() {
        assertThat(strategy.getStrategyName()).isEqualTo("SIMPLE_RANDOM");
    }

    private List<LunchMenu> createTestMenus() {
        return List.of(
                createMenu(1L, "김치찌개", Category.KOREAN, 1),
                createMenu(2L, "짜장면", Category.CHINESE, 1),
                createMenu(3L, "초밥", Category.JAPANESE, 1)
        );
    }

    private LunchMenu createMenu(Long id, String name, Category category, int weight) {
        LunchMenu menu = LunchMenu.builder()
                .name(name)
                .category(category)
                .weight(weight)
                .build();
        // Reflection으로 ID 설정 (테스트 용도)
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
