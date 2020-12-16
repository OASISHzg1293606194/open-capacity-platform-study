package com.open.capacity.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Maps;
import com.open.capacity.common.exception.service.ServiceException;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.user.dao.SysMenuDao;
import com.open.capacity.user.dao.SysRoleMenuDao;
import com.open.capacity.user.service.SysMenuService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysMenuServiceImpl implements SysMenuService {

	@Autowired
	private SysMenuDao menuDao;
 	@Autowired
	private SysRoleMenuDao roleMenuDao; 

	@Transactional
	@Override
	public void save(SysMenu menu)  throws ServiceException{
		try {
			menu.setCreateTime(new Date());
			menu.setUpdateTime(menu.getCreateTime());

			menuDao.save(menu);
			log.info("新增菜单：{}", menu);
		} catch (Exception e) {
//			BizLog.info("菜单保存处理失败", LogEntry.builder().clazz(this.getClass().getName()).method("save").error(e.getMessage()).build());
			throw new ServiceException(e) ;
		}
	}

	@Transactional
	@Override
	public void update(SysMenu menu)  throws ServiceException {
		try {
			menu.setUpdateTime(new Date());

			menuDao.updateByPrimaryKey(menu);
			log.info("修改菜单：{}", menu);
		} catch (Exception e) {
//			BizLog.info("菜单修改处理失败", LogEntry.builder().clazz(this.getClass().getName()).method("update").error(e.getMessage()).build());
			throw new ServiceException(e) ;
		}
	}

	@Transactional
	@Override
	public void delete(Long id)  throws ServiceException{
		try {
			SysMenu menu = menuDao.findById(id);
			menuDao.deleteByPrimaryKey(id);
			log.info("删除菜单：{}", menu);
		} catch (Exception e) {
//			BizLog.info("菜单删除处理失败", LogEntry.builder().clazz(this.getClass().getName()).method("delete").error(e.getMessage()).build());
			throw new ServiceException(e) ;
		}
	}

	
	@Override
	@Transactional
	public void setMenuToRole(Long roleId, Set<Long> menuIds)  throws ServiceException {
		try {
			roleMenuDao.deleteBySelective(roleId, null);
			if (!CollectionUtils.isEmpty(menuIds)) {
				roleMenuDao.saveBatch(roleId , menuIds ) ;
			}
		} catch (Exception e) {
//			BizLog.info("菜单角色处理失败", LogEntry.builder().clazz(this.getClass().getName()).method("setMenuToRole").error(e.getMessage()).build());
			throw new ServiceException(e);
		}
	}

	@Override
	public List<SysMenu> findByRoles(Set<Long> roleIds)  throws ServiceException{
		try {
			return roleMenuDao.findMenusByRoleIds(roleIds);
		} catch (Exception e) {
//			BizLog.info("角色菜单处理失败", LogEntry.builder().clazz(this.getClass().getName()).method("findByRoles").error(e.getMessage()).build());
			throw new ServiceException(e);
		}
	}

	@Override
	public List<SysMenu> findAll()  throws ServiceException{
		try {
			return menuDao.findList(Maps.newHashMap()); //查询全部菜单
		} catch (Exception e) {
//			BizLog.info("菜单列表失败", LogEntry.builder().clazz(this.getClass().getName()).method("findAll").error(e.getMessage()).build());
			throw new ServiceException(e);
		}
	}

	@Override
	public SysMenu findById(Long id)  throws ServiceException{
		try {
			return menuDao.findById(id);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Set<Long> findMenuIdsByRoleId(Long roleId)  throws ServiceException{
		try {
			return roleMenuDao.findMenuIdsByRoleId(roleId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<SysMenu> findOnes()  throws ServiceException{
		try {
			HashMap<String, Object> params = Maps.newHashMap() ;
			params.put("isMenu", 1);
			return menuDao.findList(params);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
