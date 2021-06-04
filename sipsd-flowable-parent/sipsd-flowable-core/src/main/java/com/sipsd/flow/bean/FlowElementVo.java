package com.sipsd.flow.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FlowElementVo extends FlowNodeVo {

    private String assignee;

    private List<String> groupList;
}
