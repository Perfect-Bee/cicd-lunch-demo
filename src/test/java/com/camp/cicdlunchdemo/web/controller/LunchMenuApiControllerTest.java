package com.camp.cicdlunchdemo.web.controller;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import com.camp.cicdlunchdemo.domain.repository.LunchMenuRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("LunchMenuApiController 통합 테스트")
class LunchMenuApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LunchMenuRepository lunchMenuRepository;

    @BeforeEach
    void setUp() {
        lunchMenuRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/menus")
    class GetMenus {

        @Test
        @DisplayName("전체 메뉴 목록을 조회한다")
        void returnsAllMenus() throws Exception {
            // given
            createAndSaveMenu("김치찌개", Category.KOREAN);
            createAndSaveMenu("짜장면", Category.CHINESE);

            // when
            ResultActions result = mockMvc.perform(get("/api/menus"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andDo(print());
        }

        @Test
        @DisplayName("카테고리로 필터링하여 조회한다")
        void returnsMenusByCategory() throws Exception {
            // given
            createAndSaveMenu("김치찌개", Category.KOREAN);
            createAndSaveMenu("된장찌개", Category.KOREAN);
            createAndSaveMenu("짜장면", Category.CHINESE);

            // when
            ResultActions result = mockMvc.perform(get("/api/menus")
                    .param("category", "KOREAN"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[*].category", everyItem(is("KOREAN"))));
        }

        @Test
        @DisplayName("빈 목록이면 빈 배열을 반환한다")
        void returnsEmptyArrayWhenNoMenus() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/menus"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/menus/{id}")
    class GetMenu {

        @Test
        @DisplayName("존재하는 메뉴를 조회한다")
        void returnsMenuWhenExists() throws Exception {
            // given
            LunchMenu menu = createAndSaveMenu("김치찌개", Category.KOREAN);

            // when
            ResultActions result = mockMvc.perform(get("/api/menus/{id}", menu.getId()));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("김치찌개"))
                    .andExpect(jsonPath("$.data.category").value("KOREAN"));
        }

        @Test
        @DisplayName("존재하지 않는 메뉴 조회 시 404를 반환한다")
        void returns404WhenNotExists() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/menus/{id}", 999L));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/menus")
    class CreateMenu {

        @Test
        @DisplayName("새 메뉴를 등록한다")
        void createsNewMenu() throws Exception {
            // given
            Map<String, Object> request = Map.of(
                    "name", "새 메뉴",
                    "description", "맛있는 메뉴입니다",
                    "category", "KOREAN",
                    "spicyLevel", 1,
                    "weight", 2
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/menus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("새 메뉴"))
                    .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        @DisplayName("유효하지 않은 데이터로 등록 시 400을 반환한다")
        void returns400WhenInvalidData() throws Exception {
            // given - 이름이 빈 문자열
            Map<String, Object> request = Map.of(
                    "name", "",
                    "category", "KOREAN"
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/menus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("PUT /api/menus/{id}")
    class UpdateMenu {

        @Test
        @DisplayName("메뉴를 수정한다")
        void updatesMenu() throws Exception {
            // given
            LunchMenu menu = createAndSaveMenu("김치찌개", Category.KOREAN);
            Map<String, Object> request = Map.of(
                    "name", "된장찌개",
                    "category", "KOREAN",
                    "spicyLevel", 0,
                    "weight", 3
            );

            // when
            ResultActions result = mockMvc.perform(put("/api/menus/{id}", menu.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("된장찌개"));
        }

        @Test
        @DisplayName("존재하지 않는 메뉴 수정 시 404를 반환한다")
        void returns404WhenNotExists() throws Exception {
            // given
            Map<String, Object> request = Map.of(
                    "name", "수정 메뉴",
                    "category", "KOREAN"
            );

            // when
            ResultActions result = mockMvc.perform(put("/api/menus/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/menus/{id}")
    class DeleteMenu {

        @Test
        @DisplayName("메뉴를 삭제한다")
        void deletesMenu() throws Exception {
            // given
            LunchMenu menu = createAndSaveMenu("김치찌개", Category.KOREAN);

            // when
            ResultActions result = mockMvc.perform(delete("/api/menus/{id}", menu.getId()));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            // verify deleted
            mockMvc.perform(get("/api/menus/{id}", menu.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("존재하지 않는 메뉴 삭제 시 404를 반환한다")
        void returns404WhenNotExists() throws Exception {
            // when
            ResultActions result = mockMvc.perform(delete("/api/menus/{id}", 999L));

            // then
            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/menus/recommend")
    class RecommendMenu {

        @Test
        @DisplayName("메뉴를 추천한다")
        void recommendsMenu() throws Exception {
            // given
            createAndSaveMenu("김치찌개", Category.KOREAN);
            createAndSaveMenu("짜장면", Category.CHINESE);

            // when
            ResultActions result = mockMvc.perform(get("/api/menus/recommend"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.name").exists());
        }

        @Test
        @DisplayName("메뉴가 없으면 에러 응답을 반환한다")
        void returnsErrorWhenNoMenus() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/menus/recommend"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    private LunchMenu createAndSaveMenu(String name, Category category) {
        LunchMenu menu = LunchMenu.builder()
                .name(name)
                .description("테스트 메뉴")
                .category(category)
                .spicyLevel(1)
                .weight(2)
                .build();
        return lunchMenuRepository.save(menu);
    }
}
