package com.unicom.account.handler.excelHandler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class ImportExcelTemplateHandler implements SheetWriteHandler {
    private String[] orgs;

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

        Map<Integer,String []> mapDropDown = new HashMap<>();
        mapDropDown.put(3,orgs);
        Sheet sheet = writeSheetHolder.getSheet();
        ///开始设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();//设置下拉框
        for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
            /***起始行、终止行、起始列、终止列**/
            CellRangeAddressList addressList = new CellRangeAddressList(1, 10000, entry.getKey(), entry.getKey());
            /***设置下拉框数据**/
            DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue());
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            /***处理Excel兼容性问题**/
            if (dataValidation instanceof XSSFDataValidation) {
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
            } else {
                dataValidation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(dataValidation);
        }

//        //下面定时样式的
//        Row row = sheet.getRow(0);
//        if(row!=null) {
//            Workbook workbook = writeWorkbookHolder.getWorkbook();
//            row.setHeight((short) 500);
//            for (int i = 0; i < row.getLastCellNum(); i++) {
//                sheet.setColumnWidth(i, 5000);
//                Cell cell = row.getCell(i);
//                cell.setCellStyle(setStyle(workbook));
//            }
//        }

    }


    /***
     * 设置红色字体样式
     * @param wb
     * @return
     */
    public static CellStyle setStyle(Workbook wb){
        Font dataFont = wb.createFont();
        dataFont.setColor(HSSFColor.RED.index);
        dataFont.setFontName("宋体");
        dataFont.setFontHeight((short) 240);
        dataFont.setBold(true);
        dataFont.setFontHeightInPoints((short) 10);
        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setWrapText(true);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        return dataStyle;
    }

}
