package com.unicom.account.mapper;

import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.Map;

@Mapper
public interface ImportDataMapper {

    @Insert("insert into h_import_logs (id,org_id,file_name,file_type,data_type,import_result,import_count,storeFilePath,userId)"
            + "values(#{id},#{orgId},#{fileName},#{fileType},#{dataType},#{importResult},#{importCount},#{storeFilePath},#{userId})"
            + " ON DUPLICATE KEY UPDATE import_result=values(import_result),import_count=values(import_count)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertLog(Map<String, Object> parm);


    @Insert("<script>"
            + "INSERT INTO t_project(project_id, data_type, project_name, project_type,project_status,main_product, product_line, sales_manager, sales_org, project_manager, project_org, project_p_org,log_id,in_tax_price,ex_tax_price,gross_profit,gross_margin,isKey,isResearch,isSecret"
            + ",init_create_date,init_pass_date) "
            + "values "
            + " <foreach collection='coll' item='pro' index='index' separator=','> "
            + "(#{pro.projectId},#{pro.dataType},#{pro.projcetName},#{pro.projcetType},#{pro.projectStatus},#{pro.mainProduct},#{pro.productLine},#{pro.salesManager},#{pro.salesOrg},#{pro.projectManager},#{pro.projectOrg},#{pro.projectPorg},#{pro.logId},#{pro.inTaxPrice},"
            + "#{pro.exTaxPrice},#{pro.grossProfit},#{pro.grossMargin},#{pro.isKey},#{pro.isResearch},#{pro.isSecret},#{pro.initCreateDate},#{pro.initPassDate})"
            + "</foreach> "
            + " ON DUPLICATE KEY UPDATE "
            + "project_status=values(project_status),"
            + "data_type=values(data_type),"
            + "main_product=values(main_product),"
            + "project_name=values(project_name),"
            + "sales_org=values(sales_org),"
            + "sales_manager=values(sales_manager),"
            + "project_manager=values(project_manager),"
            + "project_org=values(project_org),"
            + "project_p_org=values(project_p_org),"
            + "in_tax_price=values(in_tax_price),"
            + "ex_tax_price=values(ex_tax_price),"
            + "gross_profit=values(gross_profit),"
            + "gross_margin=values(gross_margin),"
            + "isKey=values(isKey),"
            + "isResearch=values(isResearch),"
            + "isSecret=values(isSecret),"
            + "init_create_date=values(init_create_date),"
            + "init_pass_date=values(init_pass_date),"
            + "log_id=values(log_id)"
            + " </script>")
    int insertData(@Param(value = "coll") Collection<Map<String, Object>> coll);





    @Insert("<script>"
            + "INSERT INTO h_user(name,id_number,tel,org_Id,root_org_id,org_name,password,create_user_id,remark) "
            + "values "
            + " <foreach collection='coll' item='pro' index='index' separator=','> "
            + "(#{pro.name},#{pro.idNumber,jdbcType=VARCHAR,typeHandler=com.unicom.health.handler.EncryptTypeHandler},#{pro.tel,typeHandler=com.unicom.health.handler.EncryptTypeHandler},#{pro.orgId},#{pro.rootOrgId},#{pro.orgName},#{pro.password,typeHandler=com.unicom.health.handler.AESTypeHandler},#{pro.logId},#{pro.remark})"
            + "</foreach>"
            + " ON DUPLICATE KEY UPDATE "
            + " tel=values(tel),"
            + " name=values(name),"
            + " org_Id=values(org_Id),"
            + " remark=values(remark),"
            + " root_org_id=values(root_org_id),"
            + " org_Name=values(org_Name),"
            + " create_user_id=values(create_user_id)"
            + " </script>")
    int insertTel(@Param(value = "coll") Collection<Map<String, Object>> coll);


    @Update("update h_import_logs set import_result=#{importResult},import_count=#{importCount} where id=#{id}")
    int updateLog(Map<String, Object> parm);

    @Select("<script> select a.file_name as fileName,file_type as fileType,data_type as dataType,import_result as importResult, DATE_FORMAT(a.create_time, '%Y.%m.%d %T') as createTime, "
            + "import_count as importCount,storeFilePath,b.username as username ,c.name as orgName   FROM  h_import_logs  a " +
            " LEFT JOIN sys_user b on a.userId=b.id " +
            " LEFT JOIN sys_organization c on a.org_id=c.id " +
            "<if  test=\"orgId !='' and orgId !=null\">  "+
            " where  a.org_id in(" +

            " WITH RECURSIVE td AS (\r\n" +
            "    SELECT id FROM sys_organization where id=#{orgId} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id ) " +
            " </if> "+
             " ORDER BY a.id desc  LIMIT #{offSet}, #{pageRow} </script>")
    Collection<Map<String, Object>> selectLogs(Map<String, Object> parm);

    @Select("select count(1) from h_import_logs")
    int countLogs();


}
