package com.unicom.controller;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.unicom.entity.Student;
import com.unicom.entity.User;
import com.unicom.service.UserService;

@Controller
public class UploadController {
	@Autowired
	private UserService userService;

	@RequestMapping("/upload")
	@ResponseBody
	public Map<String, Object> getUser(
			Map<String, Object> map,
			@RequestParam("file") MultipartFile file) {

		List<Student> students = new ArrayList<>();
		try {
			String EXCEL_XLS = "xls"; // Excel 2003
			String EXCEL_XLSX = "xlsx"; // Excel 2007/2010
			String name = file.getOriginalFilename();// 获取文件名
			InputStream is = file.getInputStream();
			Workbook wb = null;
			if (name.endsWith(EXCEL_XLS)) { // Excel 2003
				wb = new HSSFWorkbook(is);
			} else if (name.endsWith(EXCEL_XLSX)) { // Excel 2007/2010
				wb = new XSSFWorkbook(is);
			}
			Sheet sheet = wb.getSheetAt(0);
			int rowsNum = sheet.getLastRowNum();// 获得总行数
			/**
			 * 遍历excel表
			 * **/
			for (int i = 1; i < rowsNum - 1; i++) {
				Row rowi = sheet.getRow(i);// 获得工作薄的第i行
				Student student = new Student();
				if (rowi != null) {
					student.setSno(getCellValue(rowi.getCell(0)).toString());
					student.setSname(getCellValue(rowi.getCell(1)).toString());
					student.setMajor(getCellValue(rowi.getCell(2)).toString());
					student.setCls(getCellValue(rowi.getCell(3)).toString());
					student.setSex(getCellValue(rowi.getCell(4)).toString());
					student.setNation(getCellValue(rowi.getCell(5)).toString());
					student.setTel(getCellValue(rowi.getCell(6)).toString());
					student.setQq(getCellValue(rowi.getCell(7)).toString());
					student.setMail(getCellValue(rowi.getCell(8)).toString());
				}

				students.add(student);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("student", students);
		return map;
	}
	 /** 
     * 描述：对表格中数值进行格式化 
     */  
    public static  Object getCellValue(Cell cell){  
        Object value = null;  
        DecimalFormat df = new DecimalFormat("0");  //格式化字符类型的数字  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //日期格式化  
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字  
        switch (cell.getCellType()) {  
            case Cell.CELL_TYPE_STRING:  
                value = cell.getRichStringCellValue().getString();  
                break;  
            case Cell.CELL_TYPE_NUMERIC:  
                if("General".equals(cell.getCellStyle().getDataFormatString())){  
                    value = df.format(cell.getNumericCellValue());  
                }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){  
                    value = sdf.format(cell.getDateCellValue());  
                }else{  
                    value = df2.format(cell.getNumericCellValue());  
                }  
                break;  
            case Cell.CELL_TYPE_BOOLEAN:  
                value = cell.getBooleanCellValue();  
                break;  
            case Cell.CELL_TYPE_BLANK:  
                value = "";  
                break;  
            default:  
                break;  
        }  
        return value;  
    } 

}
