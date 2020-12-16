package com.open.capacity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.service.TestService;

import brave.Span;
import brave.Tracing;

import javax.annotation.Resource;

@RestController
public class TestController {

    /**
     * 手动埋点  可以参考美团监控项目cat
     * key
     * code
     * msg
     */
    @Resource
    Tracing tracing;
    @Resource
    TestService testService;

    @GetMapping("/hello")
    public String hello() {

        tracing.tracer().startScopedSpan("parentSpan");
        Span span = tracing.tracer().currentSpan();

        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        //手动埋点
        span.tag("class", stes[1].getClassName());
        span.tag("method", stes[1].getMethodName());
        span.tag("code", "0000");
        span.tag("msg", "success");
        testService.secondBiz();
        span.finish();
        return "hello";
    }
}
