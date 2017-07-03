package readXLC;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class sAdvanceHelper {
	
	private static XSSFWorkbook wb;

	// new reader
	public static ArrayList<String> readPlainFile(String filePath, boolean log) throws IOException {
		Path path = Paths.get(filePath);
		ArrayList<String> lines = new ArrayList<String>();
		lines.addAll(Files.readAllLines(path, StandardCharsets.UTF_8));
		for (String string : lines) {
			if (log) {
				System.out.println(string);
			}
		}
		return lines;
	}
	
	// new XLS reader
	public static ArrayList<ArrayList<String>> readXLSFile(String file) throws IOException {
		InputStream ExcelFileToRead = new FileInputStream(file);
		
		wb = new XSSFWorkbook(ExcelFileToRead);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row; 
		XSSFCell cell;
		Iterator<?> rows = sheet.rowIterator();
		
		ArrayList <ArrayList<String>> values = new ArrayList<ArrayList<String>>();

		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			Iterator<?> cells = row.cellIterator();
			Integer rowIndex = row.getRowNum();
			
			while (cells.hasNext()) {
				cell=(XSSFCell) cells.next();
				Integer columnIndex = cell.getColumnIndex();
				
				String cellString = "empty value";
				if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					if (cell.getStringCellValue().length() > 0) {
						cellString = cell.getStringCellValue();
					}
				}
				
				if (rowIndex == 0) {
					// first, create list
					ArrayList <String> listValues = new ArrayList<String>();
					listValues.add(cellString);
					values.add(listValues);
				} else {
					ArrayList <String> listValues = values.get(columnIndex);
					listValues.add(cellString);
				}
			}
		}
		return values;
	}
}
