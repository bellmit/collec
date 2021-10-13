package com.unicom.account.handler;


import com.unicom.roleRightShiro.config.DataKeyConfig;
import com.unicom.utils.AESUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.BLOB})
@MappedTypes({String.class})
public class AESTypeHandler extends BaseTypeHandler<String> {

    //private  final String aesKey = "f2c998bf7459a56de722c4060b71f9e1";
    // private final   String aesIv = "a1b9394c563604901a13f2e75c3a4327";
    private final String aesKey = DataKeyConfig.getAESKEY();
    private final String aesIv = DataKeyConfig.getAESIV();

    public AESTypeHandler() {

    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
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


    private String encrypt(String org) throws SQLException {
        if (org != null) {
            try {
                return AESUtils.encode(aesKey, org, aesIv);
            } catch (Exception e) {
                throw new SQLException("encrypt wrong!");
            }
        }
        return org;
    }

    private String decrypt(String encrypted) throws SQLException {
        if (encrypted != null) {
            try {
                encrypted = AESUtils.decode(this.aesKey, encrypted, aesIv);
            } catch (Exception e) {
                throw new SQLException("decrypt wrong!");
            }
        }
        return encrypted;
    }
}
