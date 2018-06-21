package helper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * 这是一个用于处理文件的绝对路径的相关信息的程序
 * 是用于文件传输的帮助类
 * 可以根据一组远端的绝对路径在本地建立目录层次结构
 * 并且在远端路径和本地文件间建立映射关系
 * @author 梁恒寅 从林 屈佳林
 *
 */
public class FilePathsHandler {

	private static Map<String, File> mapOfFile = new HashMap<String, File>();

	/**
	 * FilePathsHandler的构造函数
	 * @param _host
	 */
	public FilePathsHandler(Object _host) {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 用于在指定文件destination下根据filePaths数组创建文件和目录
	 * 会将每一个普通文件的源文件文件绝对路径和一个File对象绑定
	 * @param filePaths
	 * @param to
	 */
	public void createFiles(String[] filePaths, String to) {
		// TODO Auto-generated method stub
		File destination = new File(to);
        if(destination.exists())
            remove(destination);//如果存在同名文件，则先删除再写入(覆盖)
        
		//用路径名构建目录层次
		String root = filePaths[0];
        if(filePaths.length != 1) { //源文件是目录时
        	for(String filepath : filePaths) {
        		//创建File对象
        		String newDesti = to + filepath.substring(root.length()-1);//去掉所有与root相同的前缀
        		File newFile = new File(newDesti);
        		
        		//根据路径创建目录或者普通文件,并将文件和源文件路径绑定
        		if(!filepath.endsWith("\\")) { //以\结尾的路径代表目录
        			try {	//filepath对应普通文件时
						newFile.createNewFile();
						mapOfFile.put(filepath, newFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		} else { 	//filepath对应目录文件时
        			newFile.mkdirs();
        		}		
        	} 	
           
        } else {	//源文件是普通文件时
            mapOfFile.put(root, destination);
        }
	}

	/**
     * 这是一个删除文件的函数
     * 递归地删除目录文件或直接删除普通文件
     * @param file File类对象
     */
    private void remove(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();      //文件存入数组中
            //对数组内 文件/文件夹 进行递归
            for (File ftmp : files) {
                remove(ftmp);
            }
            file.delete();
        } else if (file.isFile()) {       //当源文件是普通文件时
            file.delete();
        }
    }
    
	/**
	 * 返回filePath对应的文件
	 * @param filePath
	 * @return
	 */
	public File getFileOf(String filePath) {
		// TODO Auto-generated method stub
		return mapOfFile.get(filePath);
	}

	/**
	 * 移除filePath对应的文件
	 * @param filePath
	 */
	public void removeFileOf(String filePath) {
		// TODO Auto-generated method stub
		mapOfFile.remove(filePath);
	}
}
