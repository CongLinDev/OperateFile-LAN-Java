package helper;

import java.io.File;
import java.util.ArrayList;
/**
 * 这是一个用于生成文件的绝对路径的程序
 * 是用于文件传输的帮助类
 * 可以获得一个目录文件下所有文件的绝对路径(非目录文件获得它本身的绝对路径)
 * 以一个字符串数组的形式返回
 * @author 梁恒寅 从林 屈佳林
 *
 */
public class FilePathsCreator {
	/**
	 * 返回file下的所有文件（包括目录）的绝对路径的字符串数组
	 * @param file
	 * @return
	 */
	public String[] getFilePathsOf(File file) {
		ArrayList<String> pathList = new ArrayList<String>();
		getFilePaths(file, pathList);//将文件路径放入pathList中
		
		//将pathList转化为数组
		String[] result = new String[pathList.size()];
		pathList.toArray(result);
		return result;
	}
	/**
	 * 将file下所有的文件的绝对路径放入pathList中
	 * 采用递归遍历
	 * @param file
	 * @param pathList
	 */
	private void getFilePaths(File file, ArrayList<String> pathList) {
		// TODO Auto-generated method stubs
		if(file.isDirectory()) {
			pathList.add(file.getAbsolutePath() + "\\");//以\结尾的字符串代表目录
			File[] files = file.listFiles();
			for(File ftmp : files) {
				getFilePaths(ftmp, pathList);
			}
		} else {
			pathList.add(file.getAbsolutePath());
		}
	}
}
