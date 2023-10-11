package com.icia.memberboard.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@Table(name = "board_table")
@Entity
public class BoardEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String boardTitle;

    @Column(length = 50, nullable = false)
    private String boardWriter;

    @Column(length = 50, nullable = false)
    private String boardContents;

    @Column
    private int boardHits;

    @Column
    private int fileAttached;
}
