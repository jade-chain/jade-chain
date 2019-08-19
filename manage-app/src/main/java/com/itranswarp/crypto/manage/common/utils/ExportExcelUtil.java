package com.itranswarp.crypto.manage.common.utils;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExportExcelUtil {

	
	

	   /**
		 * 表格名称字体类型
		 */
		public static final String TABLE_TITLE_FONTTYPE = "宋体";
		
		/**
		 * 表格名称字体大小
		 */
		public static final int TABLE_TITLE_FONTSIZE = 20;
		
		/**
		 * 表格名称字体加粗显示
		 */
		public static final boolean TABLE_TITLE_BOLD = true;
		
		/**
		 * 列标题字体类型
		 */
		public static final String COLUMN_TITLE_FONTTYPE = "宋体";
		
		/**
		 * 列标题字体大小
		 */
		public static final int COLUMN_TITLE_FONTSIZE = 10;
		
		/**
		 * 列标题字体加粗
		 */
		public static final boolean COLUMN_TITLE_BOLD = true;
		
		
	/**
	 * 
				List<UsersRank> download = usersRankService.download(jqgridQueryFilterVo,start,end);
				
				response.setContentType("applicationnd.ms-Excel");
				response.setHeader("Content-Disposition","attachment; filename=" + new String("用户邀请活动排名".getBytes("gbk"), "ISO8859-1") + ".xls");
				String[] titleName = new String[] { "用户ID", "邮箱","邀请用户数量","邀请用户ID"};
				String[] titleKey = new String[] { "userId", "email","invitNum","userType" };
				List<Map<?, ?>> list = UsersRankController.objListToMapList(download,titleKey);
				UsersRankController.expExlToStreamWithoutTotal("用户邀请活动排名", "用户邀请活动排名",titleName, titleKey, list,response.getOutputStream());
	 */
	
	/**
	 * 实体bean集合转map集合
	 * @param beanList 实体bean集合
	 * @param beanProperties bean属性数组
	 * @return map集合 
	 */
	public static List<Map<?, ?>> objListToMapList(List<? extends Object> beanList,String[] beanProperties){
		//List<Map<?, ?>> mapList=null;
		
			int size = beanList.size();
			List<Map<?, ?>> mapList = new ArrayList<Map<?,?>>(size);
			beanList.stream().forEach(obj ->{
				try {
					//Class clazz = obj.getClass();
					Class<? extends Object> clazz = obj.getClass();
					Map<String, Object> map = new HashMap<>();
					for (String beanProperty : beanProperties) {
						StringBuilder builder = new StringBuilder("get");
						String initial = String.valueOf(beanProperty.charAt(0)).toUpperCase();
						builder.append(initial).append(beanProperty.substring(1));
						String methodName =  builder.toString();
						map.put(beanProperty, clazz.getMethod(methodName).invoke(obj));
					}
					mapList.add(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		return mapList;
		
	}
	
	/**
	 * 将list对象转换为excel并写入到指定的输出流中<br/>
	 * list对象中存放Map对象
	 * @param tableTitle 表格名称
	 * @param sheetName Sheet名称
	 * @param titleName 各列标题(数组)
	 * @param titleKey 填充数据(key对应list中的map中的key) 
	 * @param list<Map> 填充对象
	 * @param outputStream 指定的excel输出流
	 */
	public static void expExlToStreamWithoutTotal(String tableTitle,String sheetName,String[] titleName,String[] titleKey,List<JSONObject> list,OutputStream outputStream){
		WritableWorkbook book = null;
		try {
			//创建workbook
			book = Workbook.createWorkbook(outputStream);
			//创建sheet并命名：sheetName
			WritableSheet sheet = book.createSheet(sheetName,0);
			//获取列数
			int columsCount = titleName.length;
			//各列列宽值数组
			int[] columsWidths = new int[columsCount];
			//合计项
			BigDecimal[] total = new BigDecimal[columsCount];
			//合计项是否能合计
			boolean[] unableTotal = new boolean[columsCount];
			//定义起始行数号
			int rowCount = 0;
			//创建表格名称与样式--占一行,此处合并单元格
			WritableCellFormat titleFormat = new WritableCellFormat();
			WritableFont titleFont = new WritableFont(WritableFont.createFont(ExportExcelUtil.TABLE_TITLE_FONTTYPE),ExportExcelUtil.TABLE_TITLE_FONTSIZE,ExportExcelUtil.TABLE_TITLE_BOLD?WritableFont.BOLD:WritableFont.NO_BOLD);
			titleFormat.setFont(titleFont);
			titleFormat.setAlignment(Alignment.CENTRE);;
			sheet.addCell(new Label(0,rowCount++,tableTitle,titleFormat));
			sheet.mergeCells(0, 0, columsCount, 0);//合并单元格
			//创建表格列头名称样式--占一行
			WritableCellFormat columnFormat = new WritableCellFormat();
			WritableFont columnFont = new WritableFont(WritableFont.createFont(ExportExcelUtil.COLUMN_TITLE_FONTTYPE),ExportExcelUtil.COLUMN_TITLE_FONTSIZE,ExportExcelUtil.COLUMN_TITLE_BOLD?WritableFont.BOLD:WritableFont.NO_BOLD);
			columnFormat.setFont(columnFont);
			columnFormat.setBackground(Colour.GRAY_25);
			//设置边框线
			columnFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.GRAY_50);
			columnFormat.setAlignment(Alignment.CENTRE);
			//设置序号列
			sheet.addCell(new Label(0,rowCount,"序号",columnFormat));
			for(int i=0;i<columsCount;i++){
				String title = titleName[i];
				columsWidths[i] = title.length()*3;
				sheet.addCell(new Label(i+1,rowCount,title,columnFormat));
			}
			//固定列头
			sheet.getSettings().setVerticalFreeze(2);
			/*sheet.getSettings().setAutomaticFormulaCalculation(true);*/
			//按照顺序填充数据，依据titleKey指定的顺序
			int dataSize = list.size();
			//数据列单元格样式
			WritableCellFormat cellFormat = new WritableCellFormat();
			cellFormat.setAlignment(Alignment.CENTRE);
			for(int i=0;i<dataSize;i++){
				Map<?, ?> dataContent = list.get(i);
				rowCount++;
				sheet.addCell(new Label(0,rowCount,i+1+"",cellFormat));
				for(int j=0;j<columsCount;j++){
					/**
					 * 改动过
					 */
					/*String str = JsonUtil.objToJsonStr(dataContent.get(titleKey[j]));*/
					String str = dataContent.get(titleKey[j])==null?"":dataContent.get(titleKey[j]).toString();
					int strWidth = str.length();
					int lastStrWidth = columsWidths[j];
					columsWidths[j] = lastStrWidth<strWidth?strWidth:lastStrWidth;
					
					sheet.addCell(new Label(j+1,rowCount,str,cellFormat));
					//合计项统计
					if(total[j] == null){
						total[j] = new BigDecimal("0");
					}
					if(!unableTotal[j] && 	 str.matches("[0-9]+")){
						total[j] = total[j].add(new BigDecimal(str));
					}else{
						unableTotal[j] = true;
					}
				}
			}
			
			//调整宽度
			for(int i=0;i<columsWidths.length;i++){
				/**
				 * 改动过
				 */
				/*CellView cellView = new CellView();
				cellView.setAutosize(true);
				sheet.setColumnView(i+1, cellView);*/
				sheet.setColumnView(i+1, columsWidths[i]+6);
			}
			
			//设置的密码
//			sheet.getSettings().setPassword("000000");//设置的密码
//			sheet.getSettings().setProtected(true);

			//将内容写入到输出流中
			book.write();
			book.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
