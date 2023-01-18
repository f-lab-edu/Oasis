package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String translator;
    private String publisher;
    private Date publishDate;
    private String imageUrl;
    private String description;
    private String categoryId;
}
