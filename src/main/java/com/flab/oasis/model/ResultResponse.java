package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultResponse<E extends Serializable> implements Serializable {
    private static final long serialVersionUID = 7652773062428593135L;

    private int code;
    private String message;
    private E data;
}
