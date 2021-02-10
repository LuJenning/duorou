package org.activiti.rest.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2019-03-10.
 * @email 154040976@qq.com
 */
@Data
@ApiModel("流程顺序")
public class ActivitiProcess {

    @ApiModelProperty("流程活动名称")
    String taskName;

    @ApiModelProperty("用户id")
    String userId;

    @ApiModelProperty("用户名")
    String userName;

    @ApiModelProperty("组")
    List<String> groupNames;

    @ApiModelProperty("流程线id")
    String sid;

    @ApiModelProperty("审批时间")
    String time;
}
