package com.poetry.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "writer")
public class Authors {

    @Id
    private Integer id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "simpleIntro", columnDefinition = "TEXT")
    private String simpleIntro;

    @Column(name = "detailIntro", columnDefinition = "TEXT")
    private String detailIntro;

    @Column(name = "headImageUrl", length = 100)
    private String headImageUrl;

    @Column(name = "dynasty", length = 10)
    private String dynasty;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSimpleIntro() { return simpleIntro; }
    public void setSimpleIntro(String simpleIntro) { this.simpleIntro = simpleIntro; }

    public String getDetailIntro() { return detailIntro; }
    public void setDetailIntro(String detailIntro) { this.detailIntro = detailIntro; }

    public String getHeadImageUrl() { return headImageUrl; }
    public void setHeadImageUrl(String headImageUrl) { this.headImageUrl = headImageUrl; }

    public String getDynasty() { return dynasty; }
    public void setDynasty(String dynasty) { this.dynasty = dynasty; }
}
