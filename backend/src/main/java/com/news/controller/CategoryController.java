package com.news.controller;

import com.news.dto.CategoryRequest;
import com.news.dto.Result;
import com.news.entity.Category;
import com.news.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public Result<List<Category>> getAll() {
        return Result.success(categoryService.findAll());
    }

    @PostMapping("/admin/categories")
    public Result<Category> create(@Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.create(request.getName()));
    }

    @PutMapping("/admin/categories/{id}")
    public Result<Category> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.update(id, request.getName()));
    }

    @DeleteMapping("/admin/categories/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
