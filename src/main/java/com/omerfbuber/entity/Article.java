package com.omerfbuber.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction(value = "is_deleted = false")
@Table(name = "articles", schema = "blog",
    indexes = {@Index(name = "idx_article_title", columnList = "title")})
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "article_categories", schema = "blog",
            joinColumns = @JoinColumn(name = "article_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id", nullable = false)
    )
    private Set<Category> category;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @Column(name = "is_deleted")
    private boolean is_deleted = false;
}
