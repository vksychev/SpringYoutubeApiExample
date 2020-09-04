package ru.vksychev.youtubetrends.domain;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "categories")
public class Category {
    @Id
    private Integer id;
    private String name;
}
