package com.camp.cicdlunchdemo.web.controller;

import com.camp.cicdlunchdemo.domain.entity.Category;
import com.camp.cicdlunchdemo.domain.entity.LunchMenu;
import com.camp.cicdlunchdemo.domain.exception.MenuConcurrentModificationException;
import com.camp.cicdlunchdemo.domain.exception.MenuNotFoundException;
import com.camp.cicdlunchdemo.domain.service.LunchMenuService;
import com.camp.cicdlunchdemo.web.dto.LunchMenuForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * View Controller for LunchMenu (HTML pages)
 */
@Controller
@RequiredArgsConstructor
public class LunchMenuController {

    private final LunchMenuService lunchMenuService;

    @GetMapping("/")
    public String home() {
        return "redirect:/menus";
    }

    @GetMapping("/menus")
    public String list(@RequestParam(required = false) Category category, Model model) {
        model.addAttribute("menus", lunchMenuService.findByOptionalCategory(category));
        model.addAttribute("selectedCategory", category);
        addCommonAttributes(model, "list");
        return "menu/list";
    }

    @GetMapping("/menus/new")
    public String createForm(Model model) {
        model.addAttribute("form", new LunchMenuForm());
        addCommonAttributes(model, "new");
        return "menu/form";
    }

    @PostMapping("/menus")
    public String create(@Valid @ModelAttribute("form") LunchMenuForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model, "new");
            return "menu/form";
        }
        lunchMenuService.save(form);
        addFlashMessage(redirectAttributes, "메뉴가 등록되었습니다.", true);
        return "redirect:/menus";
    }

    @GetMapping("/menus/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<LunchMenu> menuOpt = lunchMenuService.findById(id);
        if (menuOpt.isEmpty()) {
            addFlashMessage(redirectAttributes, "메뉴를 찾을 수 없습니다.", false);
            return "redirect:/menus";
        }
        model.addAttribute("form", LunchMenuForm.from(menuOpt.get()));
        model.addAttribute("menuId", id);
        addCommonAttributes(model, "edit");
        return "menu/edit";
    }

    @PutMapping("/menus/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") LunchMenuForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("menuId", id);
            addCommonAttributes(model, "edit");
            return "menu/edit";
        }
        try {
            lunchMenuService.update(id, form);
            addFlashMessage(redirectAttributes, "메뉴가 수정되었습니다.", true);
        } catch (MenuNotFoundException | MenuConcurrentModificationException e) {
            addFlashMessage(redirectAttributes, e.getMessage(), false);
        }
        return "redirect:/menus";
    }

    @DeleteMapping("/menus/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lunchMenuService.delete(id);
            addFlashMessage(redirectAttributes, "메뉴가 삭제되었습니다.", true);
        } catch (MenuNotFoundException e) {
            addFlashMessage(redirectAttributes, e.getMessage(), false);
        }
        return "redirect:/menus";
    }

    @GetMapping("/menus/recommend")
    public String recommend(Model model) {
        Optional<LunchMenu> menuOpt = lunchMenuService.recommendRandom();
        menuOpt.ifPresent(menu -> model.addAttribute("menu", menu));
        model.addAttribute("isEmpty", menuOpt.isEmpty());
        model.addAttribute("currentPage", "recommend");
        return "menu/recommend";
    }

    @GetMapping("/menus/roulette")
    public String roulette(Model model) {
        List<LunchMenu> menus = lunchMenuService.findAll();
        model.addAttribute("menus", menus);
        model.addAttribute("isEmpty", menus.isEmpty());
        model.addAttribute("currentPage", "roulette");
        return "menu/roulette";
    }

    // ===== Helper Methods =====

    private void addCommonAttributes(Model model, String currentPage) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("currentPage", currentPage);
    }

    private void addFlashMessage(RedirectAttributes redirectAttributes, String message, boolean isSuccess) {
        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("messageType", isSuccess ? "success" : "error");
    }
}
