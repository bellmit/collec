package com.unicom.collect.web.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.google.common.collect.Lists;
import com.unicom.account.handler.excelHandler.CustomSheetWriteHandler;
import com.unicom.account.handler.excelHandler.ImportExcelTemplateHandler;
import com.unicom.account.handler.excelHandler.TitleColorSheetWriteHandler;
import com.unicom.account.service.ImportDataService;
import com.unicom.collect.util.CollectConstants;
import com.unicom.common.IConstants;

import com.unicom.roleRightShiro.service.OrgService;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/collect/import")
public class ImportDataController {

	@Autowired
	private com.unicom.account.service.ImportDataService ImportDataService;

	@Autowired
	private OrgService orgService;




	@SuppressWarnings("unchecked")
	@RequiresPermissions(value = {"staffs:import"})
	@RequestMapping(value = "/addTel", method = RequestMethod.POST)
	public Map<String, Object> addStiff(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> parm) {
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		parm.put("userId", user.get("userId"));
		parm.put("orgId",user.get("orgId"));
		parm.put("isAdmin",user.get("isAdmin"));
		parm.put("rootId",user.get("rootId"));
		parm.put("rootName",user.get("rootName"));
		parm.put("head", CollectConstants.DATA_TYPE_TEL);
		parm.put("dataType", CollectConstants.DATA_TYPE_TEL);
		return this.ImportDataService.importData(file, parm, CollectConstants.DATA_TYPE_SYSTEM);
	}





	@RequiresPermissions(value = {"staffs:import"})
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		parm.put("orgId", user.get("orgId"));
		return ImportDataService.listAll(parm);
	}


	@RequiresPermissions(value = {"staffs:import"})
	@RequestMapping(value = "/validateCard", method = RequestMethod.POST)
	public Map<String, Object> validateCard(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> parm) {
		return ImportDataService.validateCard(file,parm);
	}


	@RequiresPermissions(value = {"staffs:import"})
	@RequestMapping(value = "/downloadTemp", method = RequestMethod.GET)
	public void downloadTemplage(@RequestParam Map<String,Object> parm, HttpServletResponse response){
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		Object isAdmin=user.get("isAdmin");
		Object rootName=user.get("rootName");
		Object rootId=user.get("rootId");

		Collection<Map<String,Object>> orgDatas=this.orgService.listChild(user);
		String[] orgs=new String[orgDatas.size()];
		int i=0;
		for(Map<String,Object> org:orgDatas){
			orgs[i++]=org.get("name")+"";
		}
//		ClassPathResource classPathResource = new ClassPathResource("template/template.xlsx");

		ImportExcelTemplateHandler impH=new ImportExcelTemplateHandler();
		impH.setOrgs(orgs);
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf8");
		response.setHeader("Content-Disposition", "attachment;filename=template.xlsx");
		response.addHeader("Access-Control-Expose-Headers", "Content-disposition");


		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		// ?????????????????????
		//headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setFontHeightInPoints((short)12);
		headWriteFont.setFontName("????????????");

		headWriteCellStyle.setWriteFont(headWriteFont);
		// ???????????????
		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
//		// ?????????????????? FillPatternType ???FillPatternType.SOLID_FOREGROUND ??????????????????????????????.???????????? FillPatternType?????????????????????
//		contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
//		// ????????????
//		contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//		WriteFont contentWriteFont = new WriteFont();
//		// ????????????
//		contentWriteFont.setFontHeightInPoints((short)20);
//		contentWriteCellStyle.setWriteFont(contentWriteFont);
		// ??????????????? ?????????????????? ???????????????????????? ?????????????????????????????????
		HorizontalCellStyleStrategy horizontalCellStyleStrategy =
				new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

		try {

			// ?????????????????????
			List<Integer> columns = Arrays.asList(3);
			// ????????????
			HashMap<Integer, String> annotationsMap = new HashMap<>();
			annotationsMap.put(3,"???????????????????????????????????????????????????????????????????????????????????????????????????");
			HashMap<Integer, String[]> dropDownMap = new HashMap<>();



			TitleColorSheetWriteHandler titleHandler = new TitleColorSheetWriteHandler(columns, IndexedColors.GREEN.index,annotationsMap,dropDownMap);
			CustomSheetWriteHandler cus=new CustomSheetWriteHandler();
			cus.setOrgs(orgs);
			EasyExcelFactory.write(response.getOutputStream()).head(head2(rootName))
					.registerWriteHandler(cus).registerWriteHandler(horizontalCellStyleStrategy).registerWriteHandler(titleHandler).sheet("????????????").doWrite(new ArrayList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<List<String>> head() {
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> head0 = new ArrayList<String>();
		head0.add("??? ???");
		List<String> head1 = new ArrayList<String>();
		head1.add("?????????");
		List<String> head2 = new ArrayList<String>();
		head2.add("????????????");

		List<String> head3 = new ArrayList<String>();
		head3.add("?????????");

		List<String> head4 = new ArrayList<String>();
		head4.add("???????????????");

		list.add(head0);
		list.add(head1);
		list.add(head2);
		list.add(head3);
		list.add(head4);
		return list;
	}


	private List<List<String>> head2(Object titleName) {

		if(titleName==null)
			titleName="???????????????????????????????????????";

		String basicInfo = titleName+"";

		List<List<String>> list = new ArrayList<List<String>>();

		list.add( Lists.newArrayList(basicInfo ,basicInfo,"??????(??????)") );
		list.add( Lists.newArrayList(basicInfo ,basicInfo,"?????????(??????)") );
		list.add( Lists.newArrayList(basicInfo ,basicInfo,"??????(??????)") );
		list.add( Lists.newArrayList(basicInfo ,basicInfo,"????????????(??????????????????)") );
		list.add( Lists.newArrayList(basicInfo ,basicInfo,"???????????????(??????)") );
//		List<String> head0 = new ArrayList<String>();
//		head0.add("??????");
//		List<String> head1 = new ArrayList<String>();
//		head1.add("?????????");
//		List<String> head2 = new ArrayList<String>();
//		head2.add("??????");
//
//		List<String> head3 = new ArrayList<String>();
//		head3.add("????????????");
//
//		List<String> head4 = new ArrayList<String>();
//		head4.add("??????");
//
//		list.add(head0);
//		list.add(head1);
//		list.add(head2);
//		list.add(head3);
//		list.add(head4);
		return list;
	}

}
