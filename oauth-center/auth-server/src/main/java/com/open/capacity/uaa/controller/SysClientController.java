package com.open.capacity.uaa.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.open.capacity.common.exception.controller.ControllerException;
import com.open.capacity.common.model.SysClient;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.Result;
import com.open.capacity.log.annotation.LogAnnotation;
import com.open.capacity.uaa.service.SysClientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 角色相关接口
 *
 * @author owen 624191343@qq.com
 */
@RestController
@Api(tags = "CLIENT API")
@RequestMapping("/clients")
@SuppressWarnings("all")
public class SysClientController {

    @Autowired
    private SysClientService sysClientService;


    @GetMapping
    @ApiOperation(value = "应用列表")
    @PreAuthorize("hasAuthority('client:get/clients')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public PageResult<SysClient> list(@RequestParam Map<String, Object> params) {
        try {
			return sysClientService.list(params) ;
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取应用")
    @PreAuthorize("hasAuthority('client:get/clients/{id}')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public SysClient get(@PathVariable Long id) {
        try {
			return sysClientService.getById(id);
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    @GetMapping("/all")
    @ApiOperation(value = "所有应用")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    @PreAuthorize("hasAnyAuthority('client:get/clients')")
    public List<SysClient> findList() {
        try {
			return sysClientService.findList(Maps.newHashMap());
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除应用")
    @PreAuthorize("hasAuthority('client:delete/clients')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public void delete(@PathVariable Long id) {
    	try {
			sysClientService.delete(id);
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

	@PostMapping("/saveOrUpdate")
    @ApiOperation(value = "保存或者修改应用")
    @PreAuthorize("hasAuthority('client:post/clients')")
    public Result saveOrUpdate(@RequestBody SysClient sysClient){
        try {
			return  sysClientService.saveOrUpdate(sysClient);
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }
    @PutMapping("/updateEnabled")
    @ApiOperation(value = "修改状态")
    @PreAuthorize("hasAuthority('client:post/clients')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public Result updateEnabled(@RequestBody Map<String, Object> params){
        try {
			return  sysClientService.updateEnabled(params);
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }
    
}
