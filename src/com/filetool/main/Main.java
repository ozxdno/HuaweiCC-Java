package com.filetool.main;

import com.elasticcloudservice.predict.Predict;
import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;

/**
 * 
 * 宸ュ叿鍏ュ彛
 * 
 * @version [鐗堟湰鍙�, 2017-12-8]
 * @see [鐩稿叧绫�/鏂规硶]
 * @since [浜у搧/妯″潡鐗堟湰]
 */
public class Main {
	public static void main(String[] args) {

		if (args.length != 3) {
			System.err
					.println("please input args: ecsDataPath, inputFilePath, resultFilePath");
			return;
		}

		String ecsDataPath = args[0];
		String inputFilePath = args[1];
		String resultFilePath = args[2];

		LogUtil.printLog("Begin");

		// 璇诲彇杈撳叆鏂囦欢
		String[] ecsContent = FileUtil.read(ecsDataPath, null);
		String[] inputContent = FileUtil.read(inputFilePath, null);

		// 鍔熻兘瀹炵幇鍏ュ彛
		String[] resultContents = Predict.predictVm(ecsContent, inputContent);

		
		
		// 鍐欏叆杈撳嚭鏂囦欢
		if (hasResults(resultContents)) {
			FileUtil.write(resultFilePath, resultContents, false);
		} else {
			FileUtil.write(resultFilePath, new String[] { "NA" }, false);
		}
		LogUtil.printLog("End");
	}

	private static boolean hasResults(String[] resultContents) {
		if (resultContents == null) {
			return false;
		}
		for (String contents : resultContents) {
			if (contents != null && !contents.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
