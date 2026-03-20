package com.civiora.civiora.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;   // general | maintenance | event | emergency

    @Column(nullable = false)
    private String priority;   // low | medium | high

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime date;

    public Notice() {}

    public Long          getId()       { return id; }
    public String        getTitle()    { return title; }
    public String        getCategory() { return category; }
    public String        getPriority() { return priority; }
    public String        getSummary()  { return summary; }
    public String        getDetail()   { return detail; }
    public String        getAuthor()   { return author; }
    public LocalDateTime getDate()     { return date; }

    public void setId(Long id)               { this.id       = id; }
    public void setTitle(String title)       { this.title    = title; }
    public void setCategory(String c)        { this.category = c; }
    public void setPriority(String p)        { this.priority = p; }
    public void setSummary(String s)         { this.summary  = s; }
    public void setDetail(String detail)     { this.detail   = detail; }
    public void setAuthor(String author)     { this.author   = author; }
    public void setDate(LocalDateTime date)  { this.date     = date; }
}
