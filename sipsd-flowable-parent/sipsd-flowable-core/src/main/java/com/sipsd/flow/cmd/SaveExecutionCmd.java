package com.sipsd.flow.cmd;


import com.sipsd.flow.exception.SipsdBootException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.idm.engine.impl.util.CommandContextUtil;

import java.io.Serializable;

/**
 * @author suxy
 */
public class SaveExecutionCmd implements Command<Void>, Serializable {
    private static final long serialVersionUID = 1L;
    protected ExecutionEntity entity;

    public SaveExecutionCmd(ExecutionEntity entity) {
        this.entity = entity;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (this.entity == null) {
            throw new SipsdBootException("executionEntity is null");
        } else {
            CommandContextUtil.getDbSqlSession(commandContext).insert(this.entity);
        }
        return null;
    }
}

