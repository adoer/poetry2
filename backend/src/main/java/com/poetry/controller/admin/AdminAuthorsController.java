package com.poetry.controller.admin;

import com.poetry.dto.Result;
import com.poetry.entity.Authors;
import com.poetry.exception.ResourceNotFoundException;
import com.poetry.service.AdminAuthorsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/authors")
public class AdminAuthorsController {

    private final AdminAuthorsService adminAuthorsService;

    public AdminAuthorsController(AdminAuthorsService adminAuthorsService) {
        this.adminAuthorsService = adminAuthorsService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String keyword) {
        return Result.success(adminAuthorsService.getAuthorsList(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        var author = adminAuthorsService.getAuthorById(id);
        if (author == null) throw new ResourceNotFoundException("作者不存在");
        return Result.success(author);
    }

    @PostMapping
    public Result<?> create(@RequestBody Authors author) {
        var created = adminAuthorsService.createAuthor(author);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Authors author) {
        var updated = adminAuthorsService.updateAuthor(id, author);
        if (updated == null) throw new ResourceNotFoundException("作者不存在");
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        var existing = adminAuthorsService.getAuthorById(id);
        if (existing == null) throw new ResourceNotFoundException("作者不存在");
        adminAuthorsService.deleteAuthor(id);
        return Result.success();
    }
}
