package com.yello.server.domain.statistics.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record VoteVO(
    List<VoteItem> voteItemList
) {

}
