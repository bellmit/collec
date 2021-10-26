package com.unicom.account.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * yangpeng
 */
@Data
public class HUserDataListener extends AnalysisEventListener<Map<Integer, String>> {
    private Collection<Map<String, Object>> batch;
    private Object logId;
    private int row=0;
    private boolean headFlag=true;
    private String rootName;

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext analysisContext) {
      if(headFlag){
          if(row==0){
              this.rootName=data.get(0);
          }
          if(row==2){
              headFlag=false;
          }
          row++;
          return;
      }

        batch.add(this.transMode(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    protected Map<String, Object> transMode(Map<Integer, String> data) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", data.get(0));
        model.put("idNumber", data.get(1));
        model.put("tel", data.get(2));
        model.put("orgName",data.get(3));
        model.put("remark",data.get(4));
        model.put("createUserId", logId);


        return model;
    }
}
