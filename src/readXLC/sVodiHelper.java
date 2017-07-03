package readXLC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sVodiHelper {
	private static int keyIndex = 1;
	
	/* PRINT FUNCTIONS */
	public static void printKeysList(ArrayList<ArrayList<String>> input, boolean includeDesc) {
		printValuesList(keyIndex, input, includeDesc);
	}
	
	public static void printEnglishClass(ArrayList<ArrayList<String>> input, boolean includeDesc) {
		printValuesList(keyIndex+1, input, includeDesc);
	}
	
	public static void printEnglishClassFullKeyValues(ArrayList<ArrayList<String>> input, boolean includeDesc) {
		printKeysList(input, includeDesc);
		printEnglishClass(input, includeDesc);
	}
	
	private static void printValuesList(int index, ArrayList<ArrayList<String>> input, boolean includeDesc) {
		ArrayList<String> listValue = getValuesList(index, input, includeDesc);
		if (listValue != null) {
			for (int x = 0; x < listValue.size(); x++) {
				System.out.println(listValue.get(x));
			}
		}
	}
	
	/* READING FUNCTIONS */
	private static ArrayList<String> getValuesList(int index, ArrayList<ArrayList<String>> input, boolean includeDesc) {
		for (int i = 0; i < input.size(); i++) {
			if (i == index) {
				ArrayList<String> listValue = input.get(i);
				if (includeDesc == false) listValue.remove(0);
				return listValue;
			}
		}
		return null;
	}
	
	public static ArrayList<String> getEnglishList(int index, ArrayList<ArrayList<String>> input) {
		return getValuesList(keyIndex+1, input, false);
	}
	
	public static void demo(String path) throws IOException {
		//sAdvanceHelper.readPlainFile(path, false);
		parseFile(path, true);
	}
	
	private static Map<String,String>parseFile(String path, boolean log) throws IOException {
		ArrayList<String>values = sAdvanceHelper.readPlainFile(path, false);
		Map<String, String> map = new HashMap<String, String>();
		if (values.size() > 0) {
			for (String string : values) {
				String [] part = string.split("=");
				if (part.length > 1) {
					String key = part[0].trim().replace("\"", "").trim();
					String content = part[1].trim();
					content = content.substring(1);
					content = content.substring(0,content.length()-2);
					map.put(key, content);
					
					if (log == true)
						System.out.println(key + "=" + content);
				}
			}
			return map;
		}
		return null;
	}
	
	public static void exportFileToStrings(int index, ArrayList<ArrayList<String>> input, boolean includeDesc, boolean log) {
		ArrayList<String> keyList = getValuesList(keyIndex, input, includeDesc);
		ArrayList<String> contentList = getValuesList(index, input, includeDesc);
		printValuesList(1, input, includeDesc);
		for (int i = 0; i < keyList.size(); i++) {
			
			String key = keyList.get(i);
			String content = contentList.get(i);
			if (log == true) {
				System.out.println(key + " = " + content);
			}
		}
	}
}
