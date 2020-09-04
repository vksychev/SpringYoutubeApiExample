package ru.vksychev.youtubetrends.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@Table(name = "videos")
@EqualsAndHashCode(of = {"id"})
public class Video {
    @Id
    private String id;
    private String name;

    @ManyToOne
    private User user;

    @ManyToMany(cascade = { CascadeType.ALL } ,fetch = FetchType.EAGER)
    @JoinTable(
            name = "video_region",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private Set<Region> regions = new HashSet<>();

    @ManyToOne
    private Category category;

    private Integer trendNum;
    private Integer dislikes;
    private Integer likes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;


    private Integer comments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime trendMoment;
    private Integer views;
}
