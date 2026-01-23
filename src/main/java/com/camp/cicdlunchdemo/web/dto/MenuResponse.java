package com.camp.cicdlunchdemo.web.dto;

import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final String categoryDisplayName;
    private final int spicyLevel;
    private final int weight;

    @Builder
    private MenuResponse(Long id, String name, String description, String category,
                         String categoryDisplayName, int spicyLevel, int weight) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.categoryDisplayName = categoryDisplayName;
        this.spicyLevel = spicyLevel;
        this.weight = weight;
    }

    public static MenuResponse from(LunchMenu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription() != null ? menu.getDescription() : "")
                .category(menu.getCategory().name())
                .categoryDisplayName(menu.getCategory().getDisplayName())
                .spicyLevel(menu.getSpicyLevel())
                .weight(menu.getWeight())
                .build();
    }
}
