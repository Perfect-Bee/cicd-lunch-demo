package com.camp.cicdlunchdemo.domain.repository;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LunchMenuRepository extends JpaRepository<LunchMenu, Long> {

    List<LunchMenu> findAllByOrderByCreatedAtDesc();

    List<LunchMenu> findByCategoryOrderByCreatedAtDesc(Category category);

    @Query("SELECT m FROM LunchMenu m ORDER BY FUNCTION('RAND')")
    List<LunchMenu> findAllRandomOrder();
}
