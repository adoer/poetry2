package com.poetry.controller;

import com.poetry.dto.Result;
import com.poetry.service.AuthorsService;
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
