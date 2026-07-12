package com.news.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(length = 20)
    private String id;

    @Column(length = 20)
    private String username;

    @Column(length = 20)
    private String nickname;

    @Column(length = 30)
    private String invite;

    @Column(length = 11)
    private String phone;

    @Column(length = 30)
    private String email;

    @Column(length = 200)
    private String openid;

    @Column(length = 300)
    private String password;

    @Column(length = 20)
    private String lastlogintime;

    @Column(name = "lastLoginId", length = 20)
    private String lastLoginId;

    @Column(name = "createTime", length = 20)
    private String createTime;

    @Column(name = "vipLevel", length = 10)
    private String vipLevel;

    @Column(length = 30)
    private String ip;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getInvite() { return invite; }
    public void setInvite(String invite) { this.invite = invite; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getLastlogintime() { return lastlogintime; }
    public void setLastlogintime(String lastlogintime) { this.lastlogintime = lastlogintime; }

    public String getLastLoginId() { return lastLoginId; }
    public void setLastLoginId(String lastLoginId) { this.lastLoginId = lastLoginId; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getVipLevel() { return vipLevel; }
    public void setVipLevel(String vipLevel) { this.vipLevel = vipLevel; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}
