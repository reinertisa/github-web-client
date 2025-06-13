package com.reinertisa.githubwebclient.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String title;
    private String body;
}
