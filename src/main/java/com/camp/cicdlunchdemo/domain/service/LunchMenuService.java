package com.camp.cicdlunchdemo.domain.service;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import com.camp.cicdlunchdemo.domain.exception.MenuConcurrentModificationException;
import com.camp.cicdlunchdemo.domain.exception.MenuNotFoundException;
import com.camp.cicdlunchdemo.domain.repository.LunchMenuRepository;
import com.camp.cicdlunchdemo.domain.service.recommendation.RecommendationStrategy;
import com.camp.cicdlunchdemo.domain.service.recommendation.SimpleRandomStrategy;
import com.camp.cicdlunchdemo.domain.service.recommendation.WeightedRandomStrategy;
import com.camp.cicdlunchdemo.web.dto.LunchMenuForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LunchMenuService {

    private final LunchMenuRepository lunchMenuRepository;
    private final WeightedRandomStrategy weightedRandomStrategy;
    private final SimpleRandomStrategy simpleRandomStrategy;

    public List<LunchMenu> findAll() {
        return lunchMenuRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<LunchMenu> findByCategory(Category category) {
        return lunchMenuRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    /**
     * 카테고리로 필터링 (null이면 전체 조회)
     */
    public List<LunchMenu> findByOptionalCategory(Category category) {
        return category != null ? findByCategory(category) : findAll();
    }

    public Optional<LunchMenu> findById(Long id) {
        return lunchMenuRepository.findById(id);
    }

    public LunchMenu getById(Long id) {
        return lunchMenuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
    }

    @Transactional
    public LunchMenu save(LunchMenuForm form) {
        return lunchMenuRepository.save(form.toEntity());
    }

    @Transactional
    public LunchMenu update(Long id, LunchMenuForm form) {
        LunchMenu menu = getById(id);

        // 낙관적 락: version 불일치 시 예외 발생
        if (form.getVersion() != null && !form.getVersion().equals(menu.getVersion())) {
            throw new MenuConcurrentModificationException();
        }

        menu.update(
                form.getName(),
                form.getDescription(),
                form.getCategory(),
                form.getSpicyLevel(),
                form.getWeight()
        );
        return menu;
    }

    @Transactional
    public void delete(Long id) {
        if (!lunchMenuRepository.existsById(id)) {
            throw new MenuNotFoundException(id);
        }
        lunchMenuRepository.deleteById(id);
    }

    /**
     * 단순 랜덤 추천
     */
    public Optional<LunchMenu> recommendRandom() {
        return recommend(simpleRandomStrategy, Collections.emptySet());
    }

    /**
     * 가중치 기반 + 중복 제외 랜덤 추천
     */
    public Optional<LunchMenu> recommendWeightedRandomExcluding(Set<Long> excludeIds) {
        return recommend(weightedRandomStrategy, excludeIds);
    }

    /**
     * 전략 패턴을 사용한 추천
     */
    private Optional<LunchMenu> recommend(RecommendationStrategy strategy, Set<Long> excludeIds) {
        List<LunchMenu> menus = lunchMenuRepository.findAll();
        return strategy.recommend(menus, excludeIds);
    }
}
