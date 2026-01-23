package com.camp.cicdlunchdemo.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecommendationHistoryService 단위 테스트")
class RecommendationHistoryServiceTest {

    private RecommendationHistoryService historyService;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        historyService = new RecommendationHistoryService();
        session = new MockHttpSession();
    }

    @Nested
    @DisplayName("getRecentMenuIds 메서드")
    class GetRecentMenuIds {

        @Test
        @DisplayName("세션에 기록이 없으면 빈 Set을 반환한다")
        void returnsEmptySetWhenNoHistory() {
            // when
            Set<Long> result = historyService.getRecentMenuIds(session);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("세션에 기록이 있으면 해당 기록을 반환한다")
        void returnsHistoryWhenExists() {
            // given
            historyService.addToHistory(session, 1L);
            historyService.addToHistory(session, 2L);

            // when
            Set<Long> result = historyService.getRecentMenuIds(session);

            // then
            assertThat(result).containsExactlyInAnyOrder(1L, 2L);
        }
    }

    @Nested
    @DisplayName("addToHistory 메서드")
    class AddToHistory {

        @Test
        @DisplayName("메뉴 ID를 기록에 추가한다")
        void addsMenuIdToHistory() {
            // when
            historyService.addToHistory(session, 1L);

            // then
            Set<Long> history = historyService.getRecentMenuIds(session);
            assertThat(history).contains(1L);
        }

        @Test
        @DisplayName("최대 5개까지만 기록을 유지한다")
        void maintainsMaxFiveRecords() {
            // when
            for (long i = 1; i <= 7; i++) {
                historyService.addToHistory(session, i);
            }

            // then
            Set<Long> history = historyService.getRecentMenuIds(session);
            assertThat(history).hasSize(5);
            assertThat(history).doesNotContain(1L, 2L);  // 가장 오래된 것들이 제거됨
            assertThat(history).contains(3L, 4L, 5L, 6L, 7L);
        }

        @Test
        @DisplayName("같은 ID를 다시 추가하면 순서가 갱신된다")
        void refreshesOrderWhenSameIdAdded() {
            // given
            historyService.addToHistory(session, 1L);
            historyService.addToHistory(session, 2L);
            historyService.addToHistory(session, 3L);

            // when - 1L을 다시 추가
            historyService.addToHistory(session, 1L);

            // then - 1L이 가장 최근이 됨
            for (long i = 4; i <= 6; i++) {
                historyService.addToHistory(session, i);
            }

            Set<Long> history = historyService.getRecentMenuIds(session);
            assertThat(history).contains(1L);  // 1L은 최근 것이므로 유지
            assertThat(history).doesNotContain(2L);  // 2L이 가장 오래된 것으로 제거됨
        }
    }

    @Nested
    @DisplayName("clearHistory 메서드")
    class ClearHistory {

        @Test
        @DisplayName("기록을 모두 삭제한다")
        void clearsAllHistory() {
            // given
            historyService.addToHistory(session, 1L);
            historyService.addToHistory(session, 2L);

            // when
            historyService.clearHistory(session);

            // then
            Set<Long> history = historyService.getRecentMenuIds(session);
            assertThat(history).isEmpty();
        }

        @Test
        @DisplayName("빈 세션에서도 에러 없이 동작한다")
        void worksOnEmptySession() {
            // when & then - no exception
            historyService.clearHistory(session);

            Set<Long> history = historyService.getRecentMenuIds(session);
            assertThat(history).isEmpty();
        }
    }
}
