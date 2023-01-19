package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(getBookId(), book.getBookId())
                && Objects.equals(getTitle(), book.getTitle())
                && Objects.equals(getAuthor(), book.getAuthor())
                && Objects.equals(getTranslator(), book.getTranslator())
                && Objects.equals(getPublisher(), book.getPublisher())
                && Objects.equals(getPublishDate(), book.getPublishDate())
                && Objects.equals(getImageUrl(), book.getImageUrl())
                && Objects.equals(getDescription(), book.getDescription())
                && Objects.equals(getCategoryId(), book.getCategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getBookId(), getTitle(), getAuthor(), getTranslator(), getPublisher(),
                getPublishDate(), getImageUrl(), getDescription(), getCategoryId()
        );
    }
}
