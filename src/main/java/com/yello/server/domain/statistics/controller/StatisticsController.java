package com.yello.server.domain.statistics.controller;

import static com.yello.server.global.common.SuccessCode.READ_USER_GROUP_SCHOOL_ATTACK_STATISTICS_SUCCESS;
import static com.yello.server.global.common.factory.PaginationFactory.createPageable;

import com.yello.server.domain.statistics.dto.SchoolAttackStatisticsVO;
import com.yello.server.domain.statistics.dto.response.StatisticsUserGroupSchoolAttackResponse;
import com.yello.server.domain.statistics.service.StatisticsService;
import com.yello.server.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/user-group/school-attack")
    public BaseResponse<StatisticsUserGroupSchoolAttackResponse> getSchoolAttackStatistics(
        @RequestParam(value = "page") Integer page) {
        val data = statisticsService.getSchoolAttackStatistics(createPageable(page, 10));
        return BaseResponse.success(READ_USER_GROUP_SCHOOL_ATTACK_STATISTICS_SUCCESS, data);
    }

    @GetMapping("/user-group/school-attack/{groupName}")
    public BaseResponse<SchoolAttackStatisticsVO> getSchoolAttackStatisticsByGroupName(
        @PathVariable(value = "groupName") String groupName) {
        val data = statisticsService.getSchoolAttackStatisticsByGroupName(groupName);
        return BaseResponse.success(READ_USER_GROUP_SCHOOL_ATTACK_STATISTICS_SUCCESS, data);
    }

    @GetMapping("/user-group/school-attack/group-name/{groupName}")
    public BaseResponse<StatisticsUserGroupSchoolAttackResponse> getSchoolAttackStatisticsLikeGroupName(
        @PathVariable(value = "groupName") String groupName, @RequestParam(value = "page") Integer page) {
        val data = statisticsService.getSchoolAttackStatisticsLikeGroupName(groupName, createPageable(page, 10));
        return BaseResponse.success(READ_USER_GROUP_SCHOOL_ATTACK_STATISTICS_SUCCESS, data);
    }
}
