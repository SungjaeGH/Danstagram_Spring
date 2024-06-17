package com.project.danstagram.domain.post.exception;

public class PostImageNotFoundException extends RuntimeException {
    public PostImageNotFoundException(String message) {
        super(message);
    }
}
