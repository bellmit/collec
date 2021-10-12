package com.unicom.account.handler.excelHandler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

@Setter
@Getter
public class CustomSheetWriteHandler implements SheetWriteHandler {
    private String[] orgs;

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
        // 区间设置 第一列第一行和第二行的数据。
        // 由于第一行是头，所以第一、二行的数据实际上是第二三行
        //从第五行开始 100行都是这个
        //fristRow表示 从第几行开始 到第几行结束  表格行从0开始的
        //firstCol表示 从第几列开始 到第几列结束 表格列从0开始的
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 5000, 3, 3);



        /*   解决办法从这里开始   */
        //获取一个workbook
        Workbook workbook = writeWorkbookHolder.getWorkbook();

        //定义sheet的名称
        String hiddenName = "选项列表";
        //1.创建一个隐藏的sheet 名称为 hidden
        Sheet hidden = workbook.createSheet(hiddenName);
        //2.循环赋值（为了防止下拉框的行数与隐藏域的行数相对应，将隐藏域加到结束行之后）
        for (int i = 0, length = orgs.length; i < length; i++) {
            // 3:表示你开始的行数  3表示 你开始的列数
            hidden.createRow( 2000+ i).createCell(3).setCellValue(orgs[i]);
        }
        Name category1Name = workbook.createName();
        category1Name.setNameName(hiddenName);
        //4 A1:A代表隐藏域创建第N列createCell(N)时。以A1列开始A行数据获取下拉数组
        category1Name.setRefersToFormula(hiddenName + "!A1:A" + (2000 + orgs.length));
        //5 将刚才设置的sheet引用到你的下拉列表中
        DataValidationConstraint constraint8 = helper.createFormulaListConstraint(hiddenName);
        DataValidation dataValidation3 = helper.createValidation(constraint8, cellRangeAddressList);
        writeSheetHolder.getSheet().addValidationData(dataValidation3);
    }

}
