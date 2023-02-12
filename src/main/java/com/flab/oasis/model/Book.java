package com.flab.oasis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String translator;
    private String publisher;
    private Date publishDate;
    private String imageUrl;
    private String description;
    private int categoryId;
    private String categoryName;
}
