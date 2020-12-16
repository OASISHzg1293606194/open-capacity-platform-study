package com.open.capacity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import brave.Span;
import brave.Tracing;

import javax.annotation.Resource;

@Service
@Slf4j
public class TestService {

    /**
     * 手动埋点
     * key
     * code
     * msg
     */
    @Resource
    Tracing tracing;

    public void secondBiz() {
        tracing.tracer().startScopedSpanWithParent("childSpan", tracing.tracer().currentSpan().context());
        //手动埋点
        Span chindSpan = tracing.tracer().currentSpan();
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        chindSpan.tag("class", stes[1].getClassName());
        chindSpan.tag("method", stes[1].getMethodName());
        chindSpan.tag("code", "0000");
        chindSpan.tag("msg", "success");
        chindSpan.finish();
        log.info("end tracing,id:" + chindSpan.context().traceIdString());
    }


}
