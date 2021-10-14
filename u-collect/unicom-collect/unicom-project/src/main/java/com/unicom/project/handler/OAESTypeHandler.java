package com.unicom.project.handler;

import com.unicom.utils.OLDAESUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于在mybatis中，插入更新时把指定字段进行aes编码后再入库，查询时把库中结果解码后再返回<br/>
 * 需要在mapper文件中配置:<br/>
 * 1.select:<br/>
 * resultMap>result.typeHandler="此类路径"，并指定字段名,在select语句中配置resultMap="resultMap"<br/>
 * 2.where/insert/update:<br/>
 * 在#{}中，指定#{javaField,typeHandler=此类路径}
 * 
 * @author songwencheng
 *
 */
@MappedJdbcTypes({ JdbcType.VARCHAR, JdbcType.BLOB })
@MappedTypes({ String.class })
public class OAESTypeHandler extends BaseTypeHandler<String> {

    private final String key;

    public OAESTypeHandler(String key) {
        this.key = key;
    }



    @Override
    public void setNonNullParameter(//
            PreparedStatement ps,//
            int i,//
            String parameter,//
            JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            ps.setString(i, encrypt(parameter));
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return decrypt(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return decrypt(cs.getString(columnIndex));
    }

    private String encrypt(String org) {
        if (org != null) {
            org = OLDAESUtils.aesEncrypt(org, key);
        }
        return org;
    }

    private String decrypt(String encrypted) {
        if (encrypted != null) {
            encrypted = OLDAESUtils.aesDecrypt(encrypted, key);
        }
        return encrypted;
    }

}
