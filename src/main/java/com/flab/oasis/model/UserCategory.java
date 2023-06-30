package com.flab.oasis.model;

import com.flab.oasis.constant.BookCategory;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCategory implements Serializable {
    private static final long serialVersionUID = 3812763620765883807L;

    private String uid;
    private BookCategory bookCategory;
}
