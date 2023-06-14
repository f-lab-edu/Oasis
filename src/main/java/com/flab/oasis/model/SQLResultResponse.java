package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SQLResultResponse implements Serializable {
    private static final long serialVersionUID = -4087038962392617977L;

    private boolean result;
    private String message;
}
