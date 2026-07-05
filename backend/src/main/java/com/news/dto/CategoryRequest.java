package com.news.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
