package com.project.bangjjack.domain.example.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;

import org.springframework.http.HttpStatus;

public enum PostResponseCode implements ResponseCodeInterface {

	POST_CREATED(201, HttpStatus.CREATED, "게시글이 생성되었습니다."),
	POSTS_FOUND(200, HttpStatus.OK, "게시글 목록을 조회했습니다."),
	POST_FOUND(200, HttpStatus.OK, "게시글을 조회했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;

	PostResponseCode(int code, HttpStatus status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
