package com.sipsd.flow.vo.flowable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AssigneeVo
{
    private String groupId;
    private List<User> userList;
}
