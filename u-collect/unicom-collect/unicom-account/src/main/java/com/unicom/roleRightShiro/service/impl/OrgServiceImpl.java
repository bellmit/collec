package com.unicom.roleRightShiro.service.impl;

import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.mapper.OrgMapper;
import com.unicom.roleRightShiro.service.OrgService;
import com.unicom.roleRightShiro.utils.RightContants;
import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author yangpeng
 * @version 创建时间：2020年8月18日 下午4:24:10 类说明:
 */
@Service
@Slf4j
public class OrgServiceImpl implements OrgService {
	@Autowired
	private OrgMapper orgMapper;

	public Map<String, Object> list() {
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		List<Map<String, Object>> list = orgMapper.selectAll(user);
		Object result = list.stream().filter(node -> {
			Integer i = (Integer) node.get("level");
			if (user.get("orgId") != null) {
				int level = (Integer) user.get("level");
				return i == level;
			}
			return i == 1;
		}).peek(node->node.put("children",getChild(node,list))).//map(node -> {
			//node.put("children", getChild(node, list));
		//	return node;
		/*}).*/collect(Collectors.toList());
		return ResponseUtils.responseSuccessData(result);
	}

	private Object getChild(Map<String, Object> map, List<Map<String, Object>> list) {

		return list.stream().filter(node -> {
//			Integer parentId = (Integer) node.get("parentId");
//			Integer id = (Integer) map.get("id");
//			return parentId == id;
			return Objects.equals(node.get("parentId"), map.get("id"));
		}).peek(node->node.put("children",getChild(node,list)))
//		map(node -> {
//			node.put("children", getChild(node, list));
//			return node;
//		})
		.collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> haveUser(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		int i = orgMapper.selectChindern(parm);
		if (i > 0) {
			return ResponseUtils.responseError("区域中包含有效用户，请删除后再操作");
		}

		return ResponseUtils.responseSuccessData(i);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Map<String, Object> doSubmit(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		Collection<Object> add = (Collection<Object>) parm.get("addData");
		Collection<Object> up = (Collection<Object>) parm.get("updateData");
		Collection<Object> del = (Collection<Object>) parm.get("delData");


		if (add.size() > 0)
			this.orgMapper.insertAll(parm);
		if (up.size() > 0)
			this.orgMapper.updateAll(parm);
		if (del.size() > 0) {
			int i = this.orgMapper.selectChindern(parm);
			if (i > 0) {
				return ResponseUtils.responseError("删除操作中包含有效用户，删除操作未执行！");
			}
			this.orgMapper.delete(parm);
		}

		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> add(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		String orgId=user.get("orgId")+"";
		Collection<Map<String,Object>> coldata= (Collection<Map<String, Object>>) parm.get("addData");
		if(!Objects.equals(RightContants.ADMIN_FLAG,user.get("isAdmin").toString())){
			Map<String,Object> rootInfo=orgMapper.selectRoot(user.get("orgId"));

			for(Map<String,Object> dataw:coldata){

					dataw.put("rootId",rootInfo.get("id"));
					dataw.put("tNumber",rootInfo.get("tNumber"));
					dataw.put("validity",rootInfo.get("validity"));
					dataw.put("accountNumber",rootInfo.get("accountNumber"));  //根账户人数
			}
		}else{
			for(Map<String,Object> dataw:coldata){
				if(dataw.get("parentId")!=null){
					Map<String,Object> rootInfo=orgMapper.selectRoot(dataw.get("parentId"));
					dataw.put("rootId",rootInfo.get("id"));
					dataw.put("tNumber",rootInfo.get("tNumber"));
					dataw.put("validity",rootInfo.get("validity"));
					dataw.put("accountNumber",rootInfo.get("accountNumber"));
				}
			}

		}
		try {
			this.orgMapper.insertAll(parm);
		}catch (Exception e){
			if(e instanceof SQLIntegrityConstraintViolationException ||e instanceof DuplicateKeyException){
				return ResponseUtils.responseError("组织机构名称重复，无法添加！");
			}else {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> update(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		String orgId=user.get("orgId")+"";
		Collection<Map<String,Object>> coldata= (Collection<Map<String, Object>>) parm.get("updateData");
		//如果是admin用户且更改的是根节点

		for(Map<String,Object> dataw:coldata){
			if(dataw.get("parentId")==null&&"1".equals(user.get("isAdmin")+"")){
				this.orgMapper.updateOrgTNumber(dataw);
			}
		}

		try {
			this.orgMapper.updateAll(parm);
		}catch (Exception e){
			if(e instanceof SQLIntegrityConstraintViolationException ||e instanceof DuplicateKeyException){
				return ResponseUtils.responseError("组织机构名称重复，无法修改！");
			}else {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}


		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> delete(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		int i = orgMapper.selectChindern(parm);
//		i=i+orgMapper.selectHuser(parm);
		if (i > 0) {
			return ResponseUtils.responseError("删除操作中包含有效用户，删除操作未执行！");
		}

		this.orgMapper.delete(parm);
		return ResponseUtils.responseSuccess();
	}


	public Collection<Map<String,Object>> listChild(Map<String,Object> parm){
		return this.orgMapper.selectAll(parm);
	}

	@Override
	public Integer selectInId(Map<String, Object> parms) {
		return orgMapper.selectInId(parms);
	}

	@Override
	public Map<String, Object> selectRootId(Object orgId) {
		return this.orgMapper.selectRoot(orgId);
	}

//	public static void main(String[] args) {
//		Object s="1";
//		Object c=Integer.parseInt("1");
//
//		System.out.println(Objects.equals(s,c.toString()));
//	}

}
