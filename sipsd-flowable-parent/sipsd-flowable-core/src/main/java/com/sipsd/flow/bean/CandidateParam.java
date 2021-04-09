package com.sipsd.flow.bean;

import com.sipsd.flow.bean.enums.CandidateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateParam implements Serializable {
    private static final long serialVersionUID = 994290783964598993L;

    private String id;

    private String name;

    /** 默认是用户 */
    private CandidateType type = CandidateType.USER;
}
