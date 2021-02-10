package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableName(value = "user_leave")
public class UserLeave extends BaseTask {

    @TableId(value = "id", type = IdType.UUID)
    protected String id;

    /**
     * @return id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    @Override
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    private Integer days;

    @TableField(value = "begin_time")
    private Date beginTime;

    @TableField(value = "end_time")
    private Date endTime;

    @TableField(value = "process_instance_Id")
    private String processInstanceId;

    private String status;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "update_date")
    private Date updateDate;

    @TableField(value = "update_by")
    private String updateBy;

    //***实时节点信息
    @TableField(exist = false)
    private String taskName;



    //请假单审核信息
    @TableField(exist = false)
    private List<LeaveOpinion> opinionList=new ArrayList<>();

    public void leaveOpAdd(LeaveOpinion leaveOpinion){
        this.opinionList.add(leaveOpinion);
    }
    public void leaveOpAddAll(List<LeaveOpinion> leaveOpinionList){
        this.opinionList.addAll(leaveOpinionList);
    }

    public List<LeaveOpinion> getOpinionList() {
        return opinionList;
    }

    public void setOpinionList(List<LeaveOpinion> opinionList) {
        this.opinionList = opinionList;
    }



    /**
     * @return days
     */
    public Integer getDays() {
        return days;
    }

    /**
     * @param days
     */
    public void setDays(Integer days) {
        this.days = days;
    }



    public Date getBeginTime() {
        return beginTime;
    }


    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }


    public Date getEndTime() {
        return endTime;
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}