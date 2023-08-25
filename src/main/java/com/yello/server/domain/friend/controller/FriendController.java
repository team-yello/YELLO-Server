package com.yello.server.domain.friend.controller;

import static com.yello.server.global.common.SuccessCode.ADD_FRIEND_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_FRIEND_SUCCESS;
import static com.yello.server.global.common.SuccessCode.FRIEND_SEARCH_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_FRIEND_SUCCESS;
import static com.yello.server.global.common.SuccessCode.SHUFFLE_FRIEND_SUCCESS;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableByNameSort;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableLimitTen;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.dto.response.SearchFriendResponse;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final NotificationService notificationService;

    @PostMapping("/{targetId}")
    public BaseResponse addFriend(@Valid @PathVariable Long targetId, @AccessTokenUser User user) {
        val data = friendService.addFriend(user.getId(), targetId);
        notificationService.sendFriendNotification(data);
        return BaseResponse.success(ADD_FRIEND_SUCCESS);
    }

    @GetMapping
    public BaseResponse<FriendsResponse> findAllFriend(@Valid @RequestParam Integer page, @AccessTokenUser User user) {
        val data = friendService.findAllFriends(createPageableLimitTen(page), user.getId());
        return BaseResponse.success(READ_FRIEND_SUCCESS, data);
    }

    @GetMapping("/shuffle")
    public BaseResponse<List<FriendShuffleResponse>> findShuffledFriend(@AccessTokenUser User user) {
        val friendShuffleResponse = friendService.findShuffledFriend(user.getId());
        return BaseResponse.success(SHUFFLE_FRIEND_SUCCESS, friendShuffleResponse);
    }

    @GetMapping("/recommend/school")
    public BaseResponse<RecommendFriendResponse> recommendSchoolFriend(
        @Valid @RequestParam Integer page,
        @AccessTokenUser User user
    ) {
        val data = friendService.findAllRecommendSchoolFriends(createPageableByNameSort(page),
            user.getId());
        return BaseResponse.success(READ_FRIEND_SUCCESS, data);
    }

    @PostMapping("/recommend/kakao")
    public BaseResponse<RecommendFriendResponse> recommendKakaoFriend(
        @RequestBody KakaoRecommendRequest request,
        @Valid @RequestParam Integer page,
        @AccessTokenUser User user
    ) {
        val data = friendService.findAllRecommendKakaoFriends(
            createPageableByNameSort(page),
            user.getId(),
            request
        );
        return BaseResponse.success(READ_FRIEND_SUCCESS, data);
    }

    @DeleteMapping("/{targetId}")
    public BaseResponse deleteFriend(@Valid @PathVariable Long targetId, @AccessTokenUser User user) {
        friendService.deleteFriend(user.getId(), targetId);
        return BaseResponse.success(DELETE_FRIEND_SUCCESS);
    }

    @GetMapping("/search")
    public BaseResponse<SearchFriendResponse> searchFriend(
        @AccessTokenUser User user,
        @Valid @RequestParam("page") Integer page,
        @Valid @RequestParam("keyword") String keyword
    ) {
        val data = friendService.searchFriend(
            user.getId(),
            createPageableLimitTen(page),
            keyword
        );
        return BaseResponse.success(FRIEND_SEARCH_SUCCESS, data);
    }
}
