package com.open.capacity.log.dto;

import java.util.Optional;

import org.slf4j.MDC;

import com.github.structlog4j.IToLog;
import com.open.capacity.common.auth.details.LoginAppUser;
import com.open.capacity.common.constant.TraceConstant;
import com.open.capacity.common.util.SysUserUtil;
import com.open.capacity.common.util.TokenUtil;
import com.open.capacity.log.util.TraceUtil;

import lombok.Builder;
import lombok.Data;

/**
 * 业务日志
 * @author someday
 * @create 2020年04月02日
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
@Builder
public class LogEntry implements IToLog {
   
    private String transId;
    private String path ;
    private String clazz ;
    private String method ;
    private String token ;
    private String username ;
    private String msg ;
    private String error ;
  
    
    @Override
    public Object[] toLog() {
        return new Object[] {
                "transId",  Optional.ofNullable(TraceUtil.getTrace()).orElse(""),
                "path",Optional.ofNullable(path).orElse(""),
                "clazz",Optional.ofNullable(clazz).orElse(""),
                "method",Optional.ofNullable(method).orElse(""),
                "token" , Optional.ofNullable(TokenUtil.getToken()).orElse("") ,
                "username", Optional.ofNullable(SysUserUtil.getLoginAppUser()).orElse(new LoginAppUser()).getUsername(),
                "msg" , Optional.ofNullable(msg).orElse(""),
                "error",Optional.ofNullable(error).orElse("")
                
        };
    }
}