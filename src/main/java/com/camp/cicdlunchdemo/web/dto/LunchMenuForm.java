package com.camp.cicdlunchdemo.web.dto;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LunchMenuForm {

    @NotBlank(message = "메뉴 이름은 필수입니다.")
    @Size(min = 1, max = 50, message = "메뉴 이름은 1~50자 사이여야 합니다.")
    private String name;

    @Size(max = 255, message = "설명은 255자 이내로 입력해주세요.")
    private String description;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @Min(value = 0, message = "맵기 레벨은 0 이상이어야 합니다.")
    @Max(value = 3, message = "맵기 레벨은 3 이하여야 합니다.")
    private int spicyLevel = 0;

    @Min(value = 1, message = "가중치는 1 이상이어야 합니다.")
    @Max(value = 5, message = "가중치는 5 이하여야 합니다.")
    private int weight = 1;

    private Long version;

    @Builder
    public LunchMenuForm(String name, String description, Category category, int spicyLevel, int weight, Long version) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.spicyLevel = spicyLevel;
        this.weight = weight;
        this.version = version;
    }

    public static LunchMenuForm from(LunchMenu menu) {
        return LunchMenuForm.builder()
                .name(menu.getName())
                .description(menu.getDescription())
                .category(menu.getCategory())
                .spicyLevel(menu.getSpicyLevel())
                .weight(menu.getWeight())
                .version(menu.getVersion())
                .build();
    }

    public LunchMenu toEntity() {
        return LunchMenu.builder()
                .name(this.name)
                .description(this.description)
                .category(this.category)
                .spicyLevel(this.spicyLevel)
                .weight(this.weight)
                .build();
    }
}
