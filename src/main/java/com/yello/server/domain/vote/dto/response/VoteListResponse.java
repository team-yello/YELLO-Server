package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record VoteListResponse(
    @Schema(description = "내 쪽지 전체 개수")
    Integer totalCount,
    @Schema(description = "열람권 수")
    Integer ticketCount,
    @Schema(description = "오픈한 쪽지 수")
    Integer openCount,
    @Schema(description = "오픈한 키워드 쪽지 수")
    Integer openKeywordCount,
    @Schema(description = "오픈한 초성 쪽지 수")
    Integer openNameCount,
    @Schema(description = "오픈한 전체 이름 쪽지 수")
    Integer openFullNameCount,
    @Schema(description = "내 쪽지 리스트")
    List<VoteResponse> votes
) {

    public static VoteListResponse of(VoteCountVO count, List<VoteResponse> votes, User user) {
        return VoteListResponse.builder()
            .totalCount(count.totalCount())
            .votes(votes)
            .ticketCount(user.getTicketCount())
            .openCount(count.openCount())
            .openKeywordCount(count.openKeywordCount())
            .openNameCount(count.openNameCount())
            .openFullNameCount(count.openFullNameCount())
            .build();
    }
}
