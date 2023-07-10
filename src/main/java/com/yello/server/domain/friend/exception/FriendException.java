package com.yello.server.domain.friend.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class FriendException extends CustomException {

    public FriendException(ErrorCode error) {
        super(error, "[FriendException] " + error.getMessage());
    }
}
