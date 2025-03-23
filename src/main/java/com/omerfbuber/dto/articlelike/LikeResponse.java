package com.omerfbuber.dto.articlelike;

import java.io.Serializable;

public record LikeResponse(long articleId, int likeCount) implements Serializable {
}
