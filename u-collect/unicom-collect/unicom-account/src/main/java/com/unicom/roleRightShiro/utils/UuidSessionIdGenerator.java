package com.unicom.roleRightShiro.utils;

import com.fasterxml.uuid.Generators;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月10日 下午4:30:17
 * 类说明:用于自己创建sessionId
 */
public class UuidSessionIdGenerator implements SessionIdGenerator {

	@Override
	public Serializable generateId(Session session) {
		// TODO Auto-generated method stub
		return Generators.timeBasedGenerator().generate();
	}

}
