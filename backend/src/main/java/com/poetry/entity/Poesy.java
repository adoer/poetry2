package com.poetry.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gushici")
public class Poesy {

    @Id
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "dynasty")
    private String dynasty;

    @Column(name = "writer")
    private String writer;

    @Column(name = "writerId")
    private String writerId;

    @Column(name = "writerImg")
    private String writerImg;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "type")
    private String type;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "shangxi", columnDefinition = "TEXT")
    private String shangxi;

    @Column(name = "translation", columnDefinition = "TEXT")
    private String translation;

    @Column(name = "audioUrl")
    private String audioUrl;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDynasty() { return dynasty; }
    public void setDynasty(String dynasty) { this.dynasty = dynasty; }

    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }

    public String getWriterId() { return writerId; }
    public void setWriterId(String writerId) { this.writerId = writerId; }

    public String getWriterImg() { return writerImg; }
    public void setWriterImg(String writerImg) { this.writerImg = writerImg; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getShangxi() { return shangxi; }
    public void setShangxi(String shangxi) { this.shangxi = shangxi; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}
