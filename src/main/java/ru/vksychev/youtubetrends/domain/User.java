package ru.vksychev.youtubetrends.domain;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "youtubers")
public class User {
    @Id
    private String id;

    private String name;
    private Integer subs;
}
