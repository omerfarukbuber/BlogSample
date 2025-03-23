package com.omerfbuber.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_deleted = false")
@Table(name = "categories", schema = "blog",
        indexes = {@Index(name = "idx_category_name", columnList = "name")})
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @ManyToMany(mappedBy = "category")
    private Set<Article> articles;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

}
