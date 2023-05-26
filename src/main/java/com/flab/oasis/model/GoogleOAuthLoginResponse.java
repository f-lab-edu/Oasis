package com.flab.oasis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleOAuthLoginResponse implements Serializable {
    private static final long serialVersionUID = -8270812217050935838L;

    @JsonIgnore
    JwtToken jwtToken;

    String uid;
    boolean joinState = false;
}
