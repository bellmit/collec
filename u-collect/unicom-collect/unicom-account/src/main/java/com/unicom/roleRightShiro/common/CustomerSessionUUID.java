package com.unicom.roleRightShiro.common;

import com.fasterxml.uuid.Generators;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月12日 下午3:02:51
 * 类说明:
 */
public class CustomerSessionUUID implements SessionIdGenerator {

	@Override
	public Serializable generateId(Session session) {
		// TODO Auto-generated method stub

		return Generators.timeBasedGenerator().generate().toString();
	}

}
