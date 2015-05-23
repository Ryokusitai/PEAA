package peaa.asm;

import java.util.HashMap;
import java.util.Map;

public class MethodNameList
{
	private static MethodNameList instance = new MethodNameList();
	public static MethodNameList getInstance() {
		return instance;
	}

	// デバッグ時と実際の環境での動作時ではメソッド名が変わるためその切り替えを行う
	private final boolean DEBUG = false;

	private Map<String, String> methodNameList = new HashMap<String,String>();
	private MethodNameList() {// areItemStacksEqual = func_77970_a		雛形 : methodNameList.put("","");
		methodNameList.put("areItemStacksEqual", "func_77970_a");
		methodNameList.put("stackTagCompound","field_77990_d");
		methodNameList.put("getCommandSenderName","func_70005_c_");
		methodNameList.put("getItemById","func_150899_d");
		methodNameList.put("getIdFromItem","func_150891_b");
		methodNameList.put("getItem","func_77973_b");
		methodNameList.put("areItemStackTagsEqual","func_77970_a");
		methodNameList.put("hasTagCompound","func_77942_o");
		methodNameList.put("setTagCompound","func_77982_d");
		methodNameList.put("getDisplayName","func_82833_r");
		methodNameList.put("getCommandSenderName","func_70005_c_");
	}

	public static String getName(String methodName) {
		if (instance.methodNameList.containsKey(methodName)) {
			if (instance.DEBUG) {
				return methodName;
			} else {
				return instance.methodNameList.get(methodName);
			}
		}
		throw new IllegalArgumentException(methodName + "はリストに登録されていません");
	}

}
