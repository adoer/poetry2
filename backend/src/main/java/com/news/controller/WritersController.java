package com.news.controller;

import com.news.dto.Result;
import com.news.service.AuthorsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/writers")
public class WritersController {

    private final AuthorsService authorsService;

    public WritersController(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    @GetMapping
    public Result<?> getWriters() {
        return Result.success(authorsService.getWriters());
    }
}
