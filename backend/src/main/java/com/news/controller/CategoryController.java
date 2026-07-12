package com.news.controller;

import com.news.dto.Result;
import com.news.service.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<?> getCategories() {
        return Result.success(categoryService.getCategories());
    }

    @GetMapping("/detail")
    public Result<?> getCategoryDetail(@RequestParam String category) {
        return Result.success(categoryService.getCategoryDetail(category));
    }
}
