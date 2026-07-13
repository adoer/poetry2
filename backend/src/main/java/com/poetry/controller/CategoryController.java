package com.poetry.controller;

import com.poetry.dto.Result;
import com.poetry.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<String>> getCategories() {
        return Result.success(categoryService.getCategories());
    }

    @GetMapping("/detail")
    public Result<List<Map<String, Object>>> getCategoryDetail(@RequestParam String category) {
        return Result.success(categoryService.getCategoryDetail(category));
    }
}
