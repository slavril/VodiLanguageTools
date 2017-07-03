package readXLC;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class main {
	
	static String pathString;
	
	public static Map<String, String> map = new HashMap<String, String>();
	public static Map<String, String> mergeFile = new HashMap<String, String>();
	public static Map<String, String> compare2Lang = new HashMap<String, String>();
	
	public static void saveNodeToDict(Map<String, String> mapFile, String key, String value) {
		if (key.length() > 0) {
			mapFile.put(key, value);
		}
	}
	
	// using function core functions
	public static Map<String, String> readXLSXFileWithLang(int lang, String file) throws IOException {
		Map<String, String> mapFile =new HashMap<String, String>();;
		InputStream ExcelFileToRead = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row; 
		XSSFCell cell;
		Iterator rows = sheet.rowIterator();

		while (rows.hasNext())
		{
			String engIndex = "";
			String key = "";
			row=(XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			boolean isKey = false;
			boolean newLine = false;
			while (cells.hasNext())
			{
				cell=(XSSFCell) cells.next();
				int cellIndex = cell.getColumnIndex();
				
				// key
				if (cellIndex == 1) {
					if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
					{
						if (cell.getStringCellValue().length() > 0) {
							isKey = true;
							System.out.print("\"" + cell.getStringCellValue()+"\"");
							key = cell.getStringCellValue();
						}
					}
				}
				// value
				else if (cellIndex == lang && isKey == true) {
					String value = "";
					if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
					{
						value = cell.getStringCellValue().trim();
						if(value.equalsIgnoreCase("need_content") && engIndex.length() > 0) {
							value = engIndex.trim();
						}
					}
					else if (engIndex.length() > 0) {
						value = engIndex.trim();
					}
					else {
						value = "empty value";
					}
					if (value.length() > 0) {
						newLine = true;
						System.out.print(" = \"" + value +"\";");
						saveNodeToDict(mapFile, key, value);
					}
				}
				else if (cellIndex == 2) {
					engIndex = cell.getStringCellValue();
				}
			}
			if (newLine == true)
				System.out.println();
		}
		return mapFile;
	}
	
	// write language to string file
	public static void writeToFile(String file, Map<String, String>mapFile) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(file, "UTF-8");		
		for (String key : mapFile.keySet()) {
			writer.println("\"" +key + "\" = \"" + mapFile.get(key) + "\";");
		}
		writer.close();
	}
	
	public static Map<String, String> mergeLangFromFile(String filePath, String filePath2) throws FileNotFoundException, UnsupportedEncodingException {
		Map<String, String> localDict = new HashMap<String, String>();
		ArrayList<String> listKey = new ArrayList<String>();
		ArrayList<String> listValue = new ArrayList<String>();
		
		try (InputStream in = new FileInputStream(filePath);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	String [] part = line.split("=");
		    	if(part.length > 1) {
		    		String key = part[0].trim();
		    		String value = part[1].trim();
		    		if(!key.contains("//") && key.length() > 0) {
		    			listKey.add(key);
		    			listValue.add(value);
		    			localDict.put(key,  value);
		    		}
		    	}
		    }
		    if (filePath2 == null)
		    	return localDict;
		} catch (IOException x) {
		    //System.err.println(x);
		}
		
		Map<String, String> localDict2 = new HashMap<String, String>();
		if (filePath2 != null) {
			localDict2 = mergeLangFromFile(filePath2, null);
			for (int i = 0; i < listKey.size(); i++) {
		    	if (localDict2.containsKey(listKey.get(i))) {
		    		listValue.set(i, localDict2.get(listKey.get(i)));
		    	}
			}
		}
		
		PrintWriter writer = new PrintWriter(filePath + "strings", "UTF-8");
		for (int i = 0; i < listKey.size(); i++) {
			String key = listKey.get(i);
			String value = listValue.get(i);
			writer.println(key + " = " + value);
			System.out.println("update content in many language: " + value);
		}
		writer.close();
		return null;
	}
	
	public static ArrayList<String> readLangFromFile(String filePath, String lang2Path) throws FileNotFoundException {
		ArrayList<String> array = new ArrayList<String>();
		Map<String, String> localDict = new HashMap<String, String>();
		ArrayList<String> readingKey = readLanguageKeyFromFile(filePath);
		ArrayList<String> readingLanguage = new ArrayList<String>();
		
		for (String key : readingKey) {
			localDict.put(key, "");
		}
		
		//read language
		InputStream files = new FileInputStream(lang2Path);
		try (InputStream in = new FileInputStream(lang2Path);
		    BufferedReader reader =
		      new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	String [] part = line.split("=");
		    	if(part.length > 1) {
		    		String key = part[1].trim();
		    		String keyLang = part[0].trim().replace("\"", "");
		    		if(!keyLang.contains("//")) {
			    		key = key.substring(1);
			    		key = key.substring(0,key.length()-2);
		    			
		    			if(localDict.get(keyLang) != null) {
		    				readingLanguage.add(key);
		    				localDict.put(keyLang, key);
		    			}
		    			else {
		    				//readingLanguage.add("NO_CONTENT");
		    				//localDict.put(keyLang, "NO_CONTENT");
		    			}
		    		}
		    	}
		    }

		    for (String string : localDict.keySet()) {
		    	//System.out.println(string);
		    	if(localDict.get(string).length() > 0) {
    				System.out.println(localDict.get(string));
    			}
    			else {
    				System.out.println("need_content");
    			}
			}
		    
		    return array;
		} catch (IOException x) {
		    //System.err.println(x);
		}
		return null;
	}
	
	public static ArrayList<String> printLanguageKeyFromFile(String filePath) throws FileNotFoundException {
		InputStream files = new FileInputStream(filePath);
		ArrayList<String> listKeys = new ArrayList<String>();
		ArrayList<String> listLangs = new ArrayList<String>();
		ArrayList<String> combimeKey = new ArrayList<String>();
		ArrayList<String> combimeLang = new ArrayList<String>();
		
		try (InputStream in = new FileInputStream(filePath);
		    BufferedReader reader =
		      new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	String [] part = line.split("=");
		    	if(part.length > 1) {
		    		String key = part[0].trim().replace("\"", "");
		    		String language = part[1].trim().substring(1,part[1].length()-3);
		    		if(!key.contains("//")) {
		    			boolean dup = false;
		    			for (int index = 0; index < listKeys.size(); index++) {
		    		    	if (listKeys.get(index).equals(key)&& listLangs.get(index).equals(language)) {
		    		    		dup = true;
		    		    		System.out.println(key);
		    		    		System.out.println(language);
		    		    		
		    		    		listKeys.remove(index);
		    		    		listLangs.remove(index);
		    		    		
		    		    		break;
		    		    	}
		    			}
		    			
		    			if (dup == false) {
		    				listKeys.add(key);
			    			listLangs.add(language);
		    			}
		    		}
		    	}
		    }
		    System.out.println("==============================");
		    for (String string : listKeys) {
		    	System.out.println(string);
			}
		    for (String string : listLangs) {
		    	System.out.println(string);
			}
		    return listLangs;
		} catch (IOException x) {
		    //System.err.println(x);
		}
		return null;
	}
	
	public static ArrayList<String> readLanguageKeyFromFile(String filePath) throws FileNotFoundException {
		ArrayList<String> array = new ArrayList<String>();
		try (InputStream in = new FileInputStream(filePath);
		    BufferedReader reader =
		      new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	String [] part = line.split("=");
		    	if(part.length > 1) {
		    		String key = part[0].trim().replace("\"", "");
		    		if(!key.contains("//")) {
		    			//System.out.println(key);
		    			array.add(key);
		    		}
			        
		    	}
		    }
		    return array;
		} catch (IOException x) {
		    //System.err.println(x);
		}
		return null;
	}
	
	//release class
	public static void createLanguageFile(String folderPath) throws IOException {
		//read xls
				//String x = "D:\\LANGUAGE\\1.9\\";
				String fileName = "new.xlsx";
				
				Map<String, String>en = readXLSXFileWithLang(2, folderPath+fileName);
				Map<String, String>vi = readXLSXFileWithLang(3, folderPath+fileName);
				
				Map<String, String>es = readXLSXFileWithLang(4, folderPath+fileName);
				Map<String, String>pt = readXLSXFileWithLang(5, folderPath+fileName);
				
				Map<String, String>id = readXLSXFileWithLang(6, folderPath+fileName);
				Map<String, String>ms = readXLSXFileWithLang(7, folderPath+fileName);
				
				Map<String, String>hi = readXLSXFileWithLang(8, folderPath+fileName);
				Map<String, String>zh = readXLSXFileWithLang(9, folderPath+fileName);
				
				boolean print = true;
				
				if (print == true) {
					//write to text file
					writeToFile(folderPath+"en.strings", en);
					writeToFile(folderPath+"vi.strings", vi);
					writeToFile(folderPath+"es.strings", es);
					writeToFile(folderPath+"pt.strings", pt);
					writeToFile(folderPath+"id.strings", id);
					writeToFile(folderPath+"ms.strings", ms);
					writeToFile(folderPath+"hi.strings", hi);
					writeToFile(folderPath+"zh.strings", zh);
					
					// merge
					String mainLangFolderPath = folderPath + "Languages/";
					String langName = "Localizable.strings";

					String sublangFolderENPath = mainLangFolderPath + "en.lproj/" + langName;
					String sublangFolderVIPath = mainLangFolderPath + "vi.lproj/" + langName;
					String sublangFolderESPath = mainLangFolderPath + "es.lproj/" + langName;
					String sublangFolderPTPath = mainLangFolderPath + "pt-PT.lproj/" + langName;
					String sublangFolderIDPath = mainLangFolderPath + "id.lproj/" + langName;
					String sublangFolderMSPath = mainLangFolderPath + "ms.lproj/" + langName;
					String sublangFolderHIPath = mainLangFolderPath + "hi.lproj/" + langName;
					String sublangFolderZHPath = mainLangFolderPath + "zh-Hans.lproj/" + langName;

					mergeLangFromFile(sublangFolderENPath, folderPath+"en.strings");
					mergeLangFromFile(sublangFolderVIPath, folderPath+"vi.strings");
					mergeLangFromFile(sublangFolderESPath, folderPath+"es.strings");
					mergeLangFromFile(sublangFolderPTPath, folderPath+"pt.strings");
					mergeLangFromFile(sublangFolderIDPath, folderPath+"id.strings");
					mergeLangFromFile(sublangFolderMSPath, folderPath+"ms.strings");
					mergeLangFromFile(sublangFolderHIPath, folderPath+"hi.strings");
					mergeLangFromFile(sublangFolderZHPath, folderPath+"zh.strings");
				}
	}
	
	public static void createUI() {
		JFrame frame = new JFrame("Vodi Language Tool 1.13");
		
		final JLabel label = new JLabel("Status: no file choosen");
		//frame.getContentPane().add(label);

		JButton button = new JButton("Select Folder");
		JButton processButton = new JButton("RUN");
		//button.setBounds(0, 0, 100, 40);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 120, 200);
		
		panel.add(label);
		panel.add(button);
		panel.add(processButton);

		frame.getContentPane().add(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		//frame.setSize(320, 160);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser("D:\\LANGUAGE");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(null, "Open");

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            label.setText(fc.getSelectedFile().getAbsolutePath());
		            pathString = fc.getSelectedFile().getAbsolutePath();
		        } else {
		            label.setText(fc.getSelectedFile().getAbsolutePath());
		            pathString = fc.getSelectedFile().getAbsolutePath();
		        }
			}
		});
		
		processButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				label.setText("Running..");
				try {
					createLanguageFile(pathString+"\\");
					label.setText("Done");
				} catch (IOException e) {
					e.printStackTrace();
					label.setText("Error");
				}
			}
		});
	}

	public static void main(String[] args) throws IOException {
		
		//writeXLSFile();
		//readXLSFile();
		
		//writeXLSXFile();
		//2 3 4
		//readXLSXFileWithLang(7);
		
		//Map<String, String>mFile = mergeFiles("D:/test1.xlsx", 2, "D:/test2.xlsx", 2);
		//writeToFile("D:/test1.strings", mFile);

		//compares();
		//createUI();
		//printLanguageKeyFromFile("D:/LANGUAGE/2.1.3/key.txt");
		//readLangFromFile("D:/LANGUAGE/en.strings", "D:/LANGUAGE/hi.strings");	
		//sAdvanceHelper.newRawReadFunction(true, "D:/LANGUAGE/demo/en.strings");
		sVodiHelper.exportFileToStrings(1, sAdvanceHelper.readXLSFile("D:/LANGUAGE/demo/new.xlsx"), false, true);
		//sVodiHelper.demo("D:\\LANGUAGE\\demo\\en.strings");
	}
	
	//the backup
	public static ArrayList<String> readEn(String filePath) throws FileNotFoundException {
		Map <String, String> enFiles = new HashMap<String, String>();
		ArrayList<String> array = new ArrayList<String>();
		try (InputStream in = new FileInputStream(filePath);
		    BufferedReader reader =
		      new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        //System.out.println(line);
		        array.add(line.split("=")[0].trim());
		        if (line.split("=").length > 1) {
		        	enFiles.put(line.split("=")[0].trim(), line.split("=")[1].trim());
		        }
		    }
		    return array;
		} catch (IOException x) {
		    //System.err.println(x);
		}
		return null;
	}
}
