package com.news.controller;

import com.news.dto.Result;
import com.news.service.AuthorsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/authors")
public class AuthorsController {

    private final AuthorsService authorsService;

    public AuthorsController(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    @GetMapping
    public Result<?> getAuthors(@RequestParam(required = false) Integer id,
                                 @RequestParam(defaultValue = "1") int pageNum) {
        if (id != null) {
            Map<String, Object> data = authorsService.getAuthorById(id);
            if (data == null) {
                return Result.error("作者不存在");
            }
            return Result.success(data);
        }
        return Result.success(authorsService.getAuthorsList(pageNum));
    }
}
