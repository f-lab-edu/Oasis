package com.flab.oasis.model.response;

import com.flab.oasis.model.UserProfile;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class UserProfileResponse extends BaseResponse {
    private static final long serialVersionUID = -8608627323686074086L;

    private UserProfile userProfile;
}
