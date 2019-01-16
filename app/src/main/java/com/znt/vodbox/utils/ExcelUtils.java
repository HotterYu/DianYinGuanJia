package com.znt.vodbox.utils;

import android.content.Context;


import com.znt.vodbox.model.Shopinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtils {
	public static WritableFont arial14font = null;
	public static WritableCellFormat arial14format = null;
	public static WritableFont arial10font = null;
	public static WritableCellFormat arial10format = null;
	public static WritableFont arial12font = null;
	public static WritableCellFormat arial12format = null;

	public static WritableFont arial15font = null;
	public static WritableCellFormat arial15format = null;

	public final static String UTF8_ENCODING = "UTF-8";
	public final static String GBK_ENCODING = "GBK";

	public static void format() {
		try {
			arial14font = new WritableFont(WritableFont.ARIAL, 11,WritableFont.NO_BOLD);
			arial14font.setColour(jxl.format.Colour.WHITE);
			arial14format = new WritableCellFormat(arial14font);
			arial14format.setAlignment(jxl.format.Alignment.CENTRE);
			arial14format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			arial14format.setBackground(jxl.format.Colour.GREEN);

			arial10font = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD);
			arial10format = new WritableCellFormat(arial10font);
			arial10format.setAlignment(jxl.format.Alignment.LEFT);
			arial10format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			arial10format.setBackground(jxl.format.Colour.LIGHT_GREEN);

			arial12font = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD);
			arial12format = new WritableCellFormat(arial10font);
			arial12format.setAlignment(jxl.format.Alignment.LEFT);
			arial12format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			arial12format.setBackground(jxl.format.Colour.YELLOW);

			arial15font = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD);
			arial15format = new WritableCellFormat(arial10font);
			arial15format.setAlignment(jxl.format.Alignment.LEFT);
			arial15format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			arial15format.setBackground(jxl.format.Colour.RED);
			
			/*arial12font = new WritableFont(WritableFont.ARIAL, 10);
			arial12format = new WritableCellFormat(arial12font);
			arial12format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void initExcel(String fileName, String[] colName, String adminName)
	{
		format();
		WritableWorkbook workbook = null;
		try
		{
			File file = new File(fileName);
			if (!file.exists())
			{
				file.createNewFile();
			}
			workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet(adminName, 0);
			sheet.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
			for (int col = 0; col < colName.length; col++)
			{
				if(col == 0)
					sheet.setColumnView(col, 5);
				if(col == 1)
					sheet.setColumnView(col, 10);
				if(col == 2)
					sheet.setColumnView(col, 25);
				if(col == 3)
					sheet.setColumnView(col, 40);
				if(col == 4)
					sheet.setColumnView(col, 20);
				if(col == 5)
					sheet.setColumnView(col, 25);
				if(col == 6)
					sheet.setColumnView(col, 50);
				sheet.addCell(new Label(col, 0, colName[col], arial14format));
			}
			workbook.write();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (workbook != null) {
				try {
					workbook.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> void writeObjListToExcel(List<T> objList,
											   String fileName, Context c)
	{
		if (objList != null && objList.size() > 0)
		{
			WritableWorkbook writebook = null;
			InputStream in = null;
			try
			{
				WorkbookSettings setEncode = new WorkbookSettings();
				setEncode.setEncoding(UTF8_ENCODING);
				in = new FileInputStream(new File(fileName));
				Workbook workbook = Workbook.getWorkbook(in);
				writebook = Workbook.createWorkbook(new File(fileName), workbook);
				WritableSheet sheet = writebook.getSheet(0);
				for (int j = 0; j < objList.size(); j++)
				{
					if(objList.get(j) == null)
						continue;
					ArrayList<String> list = (ArrayList<String>) objList.get(j);
					for (int i = 0; i < list.size(); i++)
					{
						String content = list.get(i);
						if(content == null)
							content = "";
						if(content.contains("长期"))
							sheet.addCell(new Label(i, j + 1, content, arial15format));
						else if(content.contains("当天"))
							sheet.addCell(new Label(i, j + 1, content, arial12format));
						else
							sheet.addCell(new Label(i, j + 1, content, arial10format));
					}
				}
				writebook.write();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (writebook != null)
				{
					try
					{
						writebook.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	public static  List<Shopinfo> read2DB(File f, Context con)
	{
		ArrayList<Shopinfo> billList = new ArrayList<Shopinfo>();
		try
		{
			Workbook course = null;
			course = Workbook.getWorkbook(f);
			Sheet sheet = course.getSheet(0);

			Cell cell = null;
			for (int i = 1; i < sheet.getRows(); i++)
			{
				Shopinfo deviceInfor = new Shopinfo();
				String code = "";
				String name = "";
				cell = sheet.getCell(0, i);
				cell = sheet.getCell(1, i);
				code = cell.getContents();
				cell = sheet.getCell(2, i);
				name = cell.getContents();
				cell = sheet.getCell(3, i);
				deviceInfor.setAddress(cell.getContents());
				//tc.setVehicle(cell.getContents());
				/*Log.d("gaolei", "Row"+i+"---------"+tc.getFood() + tc.getClothes()
						+ tc.getHouse() + tc.getVehicle());*/
				deviceInfor.setName(code + "_" + name);
				billList.add(deviceInfor);

			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return billList;
	}

	public static Object getValueByRef(Class cls, String fieldName) {
		Object value = null;
		fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName
				.substring(0, 1).toUpperCase());
		String getMethodName = "get" + fieldName;
		try {
			Method method = cls.getMethod(getMethodName);
			value = method.invoke(cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
