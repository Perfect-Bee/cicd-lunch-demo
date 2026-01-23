package com.camp.cicdlunchdemo.domain.service;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import com.camp.cicdlunchdemo.domain.exception.MenuConcurrentModificationException;
import com.camp.cicdlunchdemo.domain.exception.MenuNotFoundException;
import com.camp.cicdlunchdemo.domain.repository.LunchMenuRepository;
import com.camp.cicdlunchdemo.domain.service.recommendation.SimpleRandomStrategy;
import com.camp.cicdlunchdemo.domain.service.recommendation.WeightedRandomStrategy;
import com.camp.cicdlunchdemo.web.dto.LunchMenuForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("LunchMenuService 단위 테스트")
class LunchMenuServiceTest {

    @InjectMocks
    private LunchMenuService lunchMenuService;

    @Mock
    private LunchMenuRepository lunchMenuRepository;

    @Mock
    private WeightedRandomStrategy weightedRandomStrategy;

    @Mock
    private SimpleRandomStrategy simpleRandomStrategy;

    @Nested
    @DisplayName("findByOptionalCategory 메서드")
    class FindByOptionalCategory {

        @Test
        @DisplayName("카테고리가 null이면 전체 메뉴를 조회한다")
        void returnsAllMenusWhenCategoryIsNull() {
            // given
            List<LunchMenu> allMenus = List.of(createMenu("김치찌개"), createMenu("짜장면"));
            given(lunchMenuRepository.findAllByOrderByCreatedAtDesc()).willReturn(allMenus);

            // when
            List<LunchMenu> result = lunchMenuService.findByOptionalCategory(null);

            // then
            assertThat(result).hasSize(2);
            then(lunchMenuRepository).should().findAllByOrderByCreatedAtDesc();
            then(lunchMenuRepository).should(never()).findByCategoryOrderByCreatedAtDesc(any());
        }

        @Test
        @DisplayName("카테고리가 있으면 해당 카테고리 메뉴만 조회한다")
        void returnsMenusByCategoryWhenCategoryIsPresent() {
            // given
            Category korean = Category.KOREAN;
            List<LunchMenu> koreanMenus = List.of(createMenu("김치찌개"));
            given(lunchMenuRepository.findByCategoryOrderByCreatedAtDesc(korean)).willReturn(koreanMenus);

            // when
            List<LunchMenu> result = lunchMenuService.findByOptionalCategory(korean);

            // then
            assertThat(result).hasSize(1);
            then(lunchMenuRepository).should().findByCategoryOrderByCreatedAtDesc(korean);
            then(lunchMenuRepository).should(never()).findAllByOrderByCreatedAtDesc();
        }
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("존재하는 ID로 조회하면 메뉴를 반환한다")
        void returnsMenuWhenExists() {
            // given
            Long menuId = 1L;
            LunchMenu menu = createMenu("김치찌개");
            given(lunchMenuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            LunchMenu result = lunchMenuService.getById(menuId);

            // then
            assertThat(result.getName()).isEqualTo("김치찌개");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
        void throwsExceptionWhenNotExists() {
            // given
            Long menuId = 999L;
            given(lunchMenuRepository.findById(menuId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> lunchMenuService.getById(menuId))
                    .isInstanceOf(MenuNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("save 메서드")
    class Save {

        @Test
        @DisplayName("폼 데이터로 새 메뉴를 저장한다")
        void savesNewMenuFromForm() {
            // given
            LunchMenuForm form = LunchMenuForm.builder()
                    .name("새 메뉴")
                    .category(Category.KOREAN)
                    .spicyLevel(1)
                    .weight(2)
                    .build();

            LunchMenu savedMenu = createMenu("새 메뉴");
            given(lunchMenuRepository.save(any(LunchMenu.class))).willReturn(savedMenu);

            // when
            LunchMenu result = lunchMenuService.save(form);

            // then
            assertThat(result.getName()).isEqualTo("새 메뉴");
            then(lunchMenuRepository).should().save(any(LunchMenu.class));
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class Update {

        @Test
        @DisplayName("버전이 일치하면 메뉴를 수정한다")
        void updatesMenuWhenVersionMatches() {
            // given
            Long menuId = 1L;
            LunchMenu existingMenu = createMenuWithVersion("기존 메뉴", 1L);
            given(lunchMenuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));

            LunchMenuForm form = LunchMenuForm.builder()
                    .name("수정된 메뉴")
                    .category(Category.CHINESE)
                    .spicyLevel(2)
                    .weight(3)
                    .version(1L)
                    .build();

            // when
            LunchMenu result = lunchMenuService.update(menuId, form);

            // then
            assertThat(result.getName()).isEqualTo("수정된 메뉴");
            assertThat(result.getCategory()).isEqualTo(Category.CHINESE);
        }

        @Test
        @DisplayName("버전이 불일치하면 동시성 예외를 던진다")
        void throwsConcurrencyExceptionWhenVersionMismatch() {
            // given
            Long menuId = 1L;
            LunchMenu existingMenu = createMenuWithVersion("기존 메뉴", 2L);
            given(lunchMenuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));

            LunchMenuForm form = LunchMenuForm.builder()
                    .name("수정된 메뉴")
                    .category(Category.CHINESE)
                    .version(1L)  // 다른 버전
                    .build();

            // when & then
            assertThatThrownBy(() -> lunchMenuService.update(menuId, form))
                    .isInstanceOf(MenuConcurrentModificationException.class);
        }

        @Test
        @DisplayName("폼의 버전이 null이면 버전 체크를 건너뛴다")
        void skipsVersionCheckWhenFormVersionIsNull() {
            // given
            Long menuId = 1L;
            LunchMenu existingMenu = createMenuWithVersion("기존 메뉴", 5L);
            given(lunchMenuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));

            LunchMenuForm form = LunchMenuForm.builder()
                    .name("수정된 메뉴")
                    .category(Category.KOREAN)
                    .version(null)  // API 호출 등에서 버전 미전달
                    .build();

            // when
            LunchMenu result = lunchMenuService.update(menuId, form);

            // then
            assertThat(result.getName()).isEqualTo("수정된 메뉴");
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("존재하는 메뉴를 삭제한다")
        void deletesExistingMenu() {
            // given
            Long menuId = 1L;
            given(lunchMenuRepository.existsById(menuId)).willReturn(true);

            // when
            lunchMenuService.delete(menuId);

            // then
            then(lunchMenuRepository).should().deleteById(menuId);
        }

        @Test
        @DisplayName("존재하지 않는 메뉴 삭제 시 예외를 던진다")
        void throwsExceptionWhenMenuNotExists() {
            // given
            Long menuId = 999L;
            given(lunchMenuRepository.existsById(menuId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> lunchMenuService.delete(menuId))
                    .isInstanceOf(MenuNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("recommend 메서드")
    class Recommend {

        @Test
        @DisplayName("recommendRandom은 SimpleRandomStrategy를 사용한다")
        void recommendRandomUsesSimpleStrategy() {
            // given
            LunchMenu menu = createMenu("김치찌개");
            given(lunchMenuRepository.findAll()).willReturn(List.of(menu));
            given(simpleRandomStrategy.recommend(any(), any())).willReturn(Optional.of(menu));

            // when
            Optional<LunchMenu> result = lunchMenuService.recommendRandom();

            // then
            assertThat(result).isPresent();
            then(simpleRandomStrategy).should().recommend(any(), any());
        }

        @Test
        @DisplayName("recommendWeightedRandomExcluding은 WeightedRandomStrategy를 사용한다")
        void recommendWeightedUsesWeightedStrategy() {
            // given
            LunchMenu menu = createMenu("김치찌개");
            Set<Long> excludeIds = Set.of(1L);
            given(lunchMenuRepository.findAll()).willReturn(List.of(menu));
            given(weightedRandomStrategy.recommend(any(), any())).willReturn(Optional.of(menu));

            // when
            Optional<LunchMenu> result = lunchMenuService.recommendWeightedRandomExcluding(excludeIds);

            // then
            assertThat(result).isPresent();
            then(weightedRandomStrategy).should().recommend(any(), any());
        }
    }

    private LunchMenu createMenu(String name) {
        return LunchMenu.builder()
                .name(name)
                .category(Category.KOREAN)
                .spicyLevel(1)
                .weight(1)
                .build();
    }

    private LunchMenu createMenuWithVersion(String name, Long version) {
        LunchMenu menu = createMenu(name);
        try {
            var versionField = LunchMenu.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(menu, version);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return menu;
    }
}
