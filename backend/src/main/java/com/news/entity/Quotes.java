package com.news.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sentence")
public class Quotes {

    @Id
    private Integer id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "poetryId")
    private String poetryId;

    @Column(name = "poetryName")
    private String poetryName;

    @Column(name = "writer")
    private String writer;

    @Column(name = "writerId")
    private String writerId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPoetryId() { return poetryId; }
    public void setPoetryId(String poetryId) { this.poetryId = poetryId; }

    public String getPoetryName() { return poetryName; }
    public void setPoetryName(String poetryName) { this.poetryName = poetryName; }

    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }

    public String getWriterId() { return writerId; }
    public void setWriterId(String writerId) { this.writerId = writerId; }
}
