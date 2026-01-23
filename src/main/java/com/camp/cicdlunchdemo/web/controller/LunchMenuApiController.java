package com.camp.cicdlunchdemo.web.controller;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import com.camp.cicdlunchdemo.domain.service.LunchMenuService;
import com.camp.cicdlunchdemo.domain.service.RecommendationHistoryService;
import com.camp.cicdlunchdemo.web.dto.ApiResponse;
import com.camp.cicdlunchdemo.web.dto.LunchMenuForm;
import com.camp.cicdlunchdemo.web.dto.MenuResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * RESTful API Controller for LunchMenu
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class LunchMenuApiController {

    private final LunchMenuService lunchMenuService;
    private final RecommendationHistoryService historyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenus(
            @RequestParam(required = false) Category category) {

        List<MenuResponse> response = lunchMenuService.findByOptionalCategory(category).stream()
                .map(MenuResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenu(@PathVariable Long id) {
        return lunchMenuService.findById(id)
                .map(menu -> ResponseEntity.ok(ApiResponse.success(MenuResponse.from(menu))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("메뉴를 찾을 수 없습니다.")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(
            @Valid @RequestBody LunchMenuForm form) {

        LunchMenu savedMenu = lunchMenuService.save(form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("메뉴가 등록되었습니다.", MenuResponse.from(savedMenu)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody LunchMenuForm form) {

        LunchMenu updatedMenu = lunchMenuService.update(id, form);
        return ResponseEntity.ok(ApiResponse.success("메뉴가 수정되었습니다.", MenuResponse.from(updatedMenu)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        lunchMenuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("메뉴가 삭제되었습니다."));
    }

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<MenuResponse>> recommendMenu(HttpSession session) {
        Set<Long> recentMenuIds = historyService.getRecentMenuIds(session);
        Optional<LunchMenu> menuOpt = lunchMenuService.recommendWeightedRandomExcluding(recentMenuIds);

        if (menuOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("추천할 메뉴가 없습니다."));
        }

        LunchMenu menu = menuOpt.get();
        historyService.addToHistory(session, menu.getId());

        return ResponseEntity.ok(ApiResponse.success(MenuResponse.from(menu)));
    }

    @DeleteMapping("/recommend/history")
    public ResponseEntity<ApiResponse<Void>> resetHistory(HttpSession session) {
        historyService.clearHistory(session);
        return ResponseEntity.ok(ApiResponse.success("추천 기록이 초기화되었습니다."));
    }
}
