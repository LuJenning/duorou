package com.len.mapper;

import com.len.base.BaseMapper;
import com.len.entity.ActAssignee;

public interface ActAssigneeMapper extends BaseMapper<ActAssignee> {
    int deleteByNodeId(String nodeId);
}