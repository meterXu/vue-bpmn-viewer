package com.sipsd.flow.fegin.vo;

import com.sipsd.flow.vo.flowable.AssigneeVo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FlowElementDto
{
    private List<String> assigneeList;
    private List<AssigneeVo> groupList;
    private String flowNodeId;
    private String flowNodeName;
    private String flowElementType;
}
