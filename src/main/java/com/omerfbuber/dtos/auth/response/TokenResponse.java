package com.omerfbuber.dtos.auth.response;

import java.io.Serializable;
import java.util.Date;

public record TokenResponse(String token, Date expiration) implements Serializable {
}
