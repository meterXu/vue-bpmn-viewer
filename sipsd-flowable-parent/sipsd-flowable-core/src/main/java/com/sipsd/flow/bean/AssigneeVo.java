package com.sipsd.flow.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AssigneeVo{
    private String groupId;
    private List<User> userList;
}
