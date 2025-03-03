package com.omerfbuber.dto.auth;

import java.io.Serializable;
import java.util.Date;

public record TokenResponse(String accessToken, String refreshToken,
                            Date accessTokenExpire, Date refreshTokenExpire) implements Serializable {
}
