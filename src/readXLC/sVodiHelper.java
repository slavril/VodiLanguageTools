package readXLC;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class sVodiHelper {
	private static int keyIndex = 1;
	private ArrayList<ArrayList<String>> data;
	private String folderPath;
	
	// INI
	public sVodiHelper(String folderPath) throws IOException {
		this.folderPath = folderPath;
		String xlsFilePath = this.folderPath + "new.xlsx";
		setData(sAdvanceHelper.readXLSFile(xlsFilePath));
	}
	
	public String process() throws IOException {
		String langName = "Localizable.strings";
		String languageFolderPath = this.folderPath + "Languages/";
		
		this.mergeAndWrite("en", languageFolderPath + "en.lproj/" + langName);
		this.mergeAndWrite("vi", languageFolderPath + "vi.lproj/" + langName);
		this.mergeAndWrite("es", languageFolderPath + "es.lproj/" + langName);
		this.mergeAndWrite("pt", languageFolderPath + "pt-PT.lproj/" + langName);
		this.mergeAndWrite("id", languageFolderPath + "id.lproj/" + langName);
		this.mergeAndWrite("ms", languageFolderPath + "ms.lproj/" + langName);
		this.mergeAndWrite("hi", languageFolderPath + "hi.lproj/" + langName);
		this.mergeAndWrite("zh", languageFolderPath + "zh-Hans.lproj/" + langName);
		
		System.out.println("-Success-");
		return "Success";
	}

	public ArrayList<ArrayList<String>> getData() {
		return data;
	}

	public void setData(ArrayList<ArrayList<String>> data) {
		this.data = data;
	}
	
	public static int languageIndex(String languageKey) {
		switch (languageKey) {
		case "en":
			return 2;
		case "vi":
			return 3;
		case "es":
			return 4;
		case "pt":
			return 5;
		case "id":
			return 6;
		case "ms":
			return 7;
		case "hi":
			return 8;
		case "zh":
			return 9;
		default:
			break;
		}
		return 1;
	}
	
	/* READING FUNCTIONS */
	private static ArrayList<String> getValuesList(int index, ArrayList<ArrayList<String>> input, boolean includeDesc, boolean log) {
		for (int i = 0; i < input.size(); i++) {
			if (i == index) {
				ArrayList<String> listValue = new ArrayList<String>(input.get(i));
				if (includeDesc == false) listValue.remove(0);
				return listValue;
			}
		}
		return null;
	}
	
	/**
	 * parse file from STRINGS to MAP OBJECT with key is KEY and value is CONTENT, 
	 * this method is for read and analyze origin language file support compare new and older one.
	 * @param path
	 * @param log
	 * @return
	 * @throws IOException
	 */
	private static LinkedHashMap<String,String>parseFile(String path, boolean log) throws IOException {
		ArrayList<String>values = sAdvanceHelper.readPlainFile(path, false);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (values.size() > 0) {
			for (String string : values) {
				String [] part = string.split("=");
				if (part.length > 1) {
					String key = part[0].trim().replace("\"", "").trim();
					String content = part[1].trim();
					map.put(key, content);
					
					if (log == true)
						System.out.println(key + "=" + content);
				}
			}
			return map;
		}
		return null;
	}
	
	public static void printKey(String path) throws IOException {
		ArrayList<String>values = sAdvanceHelper.readPlainFile(path, false);
		if (values.size() > 0) {
			for (String string : values) {
				String [] part = string.split("=");
				if (part.length > 1) {
					String key = part[0].trim().replace("\"", "").trim();
					
					System.out.println(key);
				}
			}
			for (String string : values) {
				String [] part = string.split("=");
				if (part.length > 1) {
					String content = part[1].trim();
					
					System.out.println(content);
				}
			}
		}
	}
	
	/**
	 * merge and write to local file
	 * @param lang
	 * @param originFilePath
	 * @return
	 * @throws IOException
	 */
	private String mergeAndWrite(String lang, String originFilePath) throws IOException {
		LinkedHashMap<String, String> map = this.mergeFile(lang, originFilePath, true);
		sVodiHelper.writePlainFile(this.folderPath+lang+"_m.strings", map, false);
		
		return "Success";
	}
	
	/**
	 * merge and return a new map object
	 * @param lang
	 * @param originFilePath
	 * @param log
	 * @return
	 * @throws IOException
	 */
	private LinkedHashMap<String, String> mergeFile(String lang, String originFilePath, boolean log) throws IOException {
		ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>(this.data);
		LinkedHashMap<String, String> originMap = new LinkedHashMap<String, String>(parseFile(originFilePath, false));
		//get the keys
		ArrayList<String> newKeys = getValuesList(sVodiHelper.keyIndex, input, false, log);
		//get the contents
		ArrayList<String> newContents = getValuesList(sVodiHelper.languageIndex(lang), input, false, log);
		
		for (int i = 0; i < newKeys.size(); i++) {
			String key = newKeys.get(i);
			String newContent = newContents.get(i);
			String oldContent = originMap.get(key);
			
			if (oldContent != null && !oldContent.equals(newContent) && !newContent.equals("empty value")) {
				originMap.replace(key, "\""+ newContent + "\";");
				if (log)
					System.out.println("replace new content: " + originMap.get(key) + "-" + oldContent + "-" + newContent); 
			}
			else if (oldContent == null) {
				originMap.put(key,"\""+ newContent+ "\";");
				if (log)
					System.out.println("put new content: " + originMap.get(key)); 
			}
			if (log) 
				System.out.println(key +": " + originMap.get(key)); 
		}
		return originMap;
	}
	
	/**
	 * write MAP HASH to local language file
	 * @param filePath
	 * @param contents
	 * @param log
	 * @throws IOException
	 */
	private static void writePlainFile(String filePath, LinkedHashMap<String,String> contents, boolean log) throws IOException {
		Path path = Paths.get(filePath);
		ArrayList<String>printOutList = new ArrayList<String>();
		for (String key : contents.keySet()) {
			String printContent = contents.get(key);
			String printOut = "\"" + key + "\" = " + printContent;
			printOutList.add(printOut);
			if (log) 
				System.out.println(printOut);
		}
		Files.write(path, printOutList, StandardCharsets.UTF_8);
	}
}
