package com.open.capacity.uaa.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.exception.controller.ControllerException;
import com.open.capacity.common.model.SysClient;
import com.open.capacity.common.model.SysService;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.Result;
import com.open.capacity.log.annotation.LogAnnotation;
import com.open.capacity.uaa.service.SysServiceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: [gitgeek]
 * @Date: [2018-08-23 16:20]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.zzg]
 */
@Slf4j
@RestController
@Api(tags = "SERVICE API")
@RequestMapping("/services")
@SuppressWarnings("all")
public class SysServiceController {

    @Autowired
    private SysServiceService sysServiceService;

    /**
     * 查询所有服务
     * @return
     */
    @ApiOperation(value = "查询所有服务")
    @GetMapping("/findAlls")
    @PreAuthorize("hasAuthority('service:get/service/findAlls')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public PageResult<SysService> findAlls() {
        try {
			List<SysService> list = sysServiceService.findAll();
			return PageResult.<SysService>builder().data(list).code(0).count((long)list.size()).build() ;
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    /**
     * 获取服务以及顶级服务
     * @return
     */
    @ApiOperation(value = "获取服务以及顶级服务")
    @GetMapping("/findOnes")
    @PreAuthorize("hasAuthority('service:get/service/findOnes')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public PageResult<SysService> findOnes(){
        try {
			List<SysService> list = sysServiceService.findOnes();
			return PageResult.<SysService>builder().data(list).code(0).count((long)list.size()).build() ;
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    /**
     * 删除服务
     * @param id
     * @return
     */
    
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除服务")
    @PreAuthorize("hasAuthority('service:delete/service/{id}')")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public Result delete(@PathVariable Long id){
        try {
			sysServiceService.delete(id);
			return Result.succeed("操作成功");
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    
    @ApiOperation(value = "新增服务")
    @PostMapping("/saveOrUpdate")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    @PreAuthorize("hasAnyAuthority('service:post/saveOrUpdate')")
    public Result saveOrUpdate(@RequestBody SysService service) {
        try{
            if (service.getId() != null){
                sysServiceService.update(service);
            }else {
                sysServiceService.save(service);
            }
            return Result.succeed("操作成功");
        }catch (Exception ex){
        	throw new ControllerException(ex);
        }
    }

    @ApiOperation(value = "根据clientId获取对应的服务")
    @GetMapping("/{clientId}/services")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public List<Map<String, Object>> findServicesByclientId(@PathVariable Long clientId) {
        try {
			Set<Long> clientIds = new HashSet<Long>();
			//初始化应用
			clientIds.add(clientId);
			List<SysService> clientService = sysServiceService.findByClient(clientIds);
			List<SysService> allService = sysServiceService.findAll();
			List<Map<String, Object>> authTrees = new ArrayList<>();
			Map<Long,SysService> clientServiceMap = clientService.stream().collect(Collectors.toMap(SysService::getId,SysService->SysService));
			for (SysService sysService: allService) {
			    Map<String, Object> authTree = new HashMap<>();
			    authTree.put("id",sysService.getId());
			    authTree.put("name",sysService.getName());
			    authTree.put("pId",sysService.getParentId());
			    authTree.put("open",true);
			    authTree.put("checked", false);
			    if (clientServiceMap.get(sysService.getId())!=null){
			        authTree.put("checked", true);
			    }
			    authTrees.add(authTree);
			}

			return  authTrees;
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }

    @PostMapping("/granted")
    @LogAnnotation(module="auth-server",recordRequestParam=false)
    public Result setMenuToClient(@RequestBody SysClient sysClient) {
        try {
			sysServiceService.setMenuToClient(sysClient.getId(), sysClient.getServiceIds());
			return Result.succeed("操作成功");
		} catch (Exception e) {
			throw new ControllerException(e);
		}
    }















}
