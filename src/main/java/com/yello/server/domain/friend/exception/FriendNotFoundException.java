package com.yello.server.domain.friend.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class FriendNotFoundException extends CustomException {

    public FriendNotFoundException(ErrorCode error) {
        super(error, "[FriendNotFoundException] " + error.getMessage());
    }
}
