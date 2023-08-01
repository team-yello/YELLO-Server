package com.yello.server.small.group;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.repository.SchoolRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;

public class FakeSchoolRepository implements SchoolRepository {

  @Override
  public School findById(Long id) {
    return null;
  }

  @Override
  public Integer countDistinctSchoolNameContaining(String schoolName) {
    return null;
  }

  @Override
  public List<String> findDistinctSchoolNameContaining(String schoolName, Pageable pageable) {
    return null;
  }

  @Override
  public Integer countAllBySchoolNameContaining(String schoolName, String departmentName) {
    return null;
  }

  @Override
  public List<School> findAllBySchoolNameContaining(String schoolName, String departmentName,
      Pageable pageable) {
    return null;
  }
}
