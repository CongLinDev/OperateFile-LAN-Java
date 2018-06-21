package client;

import helper.CheckMD5;
import helper.FilePathsCreator;
import helper.FilePathsHandler;
import operation.FileTransferService;

import java.io.*;
import java.rmi.RemoteException;

public class FileTransferHandler {
	private static final int BSIZE = 1024 * 1024;
    private static byte[] buffer = new byte[BSIZE];
    private FileTransferService fts;
    private static FilePathsHandler helper;
    
	public FileTransferHandler(FileTransferService _fts) {
		// TODO Auto-generated constructor stub
		this.fts = _fts;
		helper = new FilePathsHandler(this);
	}

	/**
     * 这是一个下载文件/目录文件的函数
     * @param from 表示源文件在服务端路径
     * @param to 表示本地的目的路径
     * @throws RemoteException
     */
    public void download(String from, String to) throws RemoteException {
        //用路径名构建目录层次
        String[] filePaths = fts.getFilePaths(from);
        helper.createFiles(filePaths, to);
        
        //对每个非目录文件，调用函数下载
    	for(String filepath : filePaths) {							
    		if(!filepath.endsWith("\\")) { //以\结尾的路径代表目录
    			File destination = helper.getFileOf(filepath);
    			downloadFile(filepath, destination);
    		} 
    	}
    }

    /**
     * 用于下载普通文件
     * @param from 表示源文件路径名
     * @param destination 目的路径对应的File对象
     * @throws RemoteException
     */
    private void downloadFile(String from, File destination) throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println(destination.getAbsolutePath() + " is downloading...");
        String md5 = fts.downloadFileStart(from);//服务端打开输入流，并返回所下载文件的MD5
        
        //下载文件
        byte[] downloadBuffer = null;
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(destination));
            while(fts.downloadFileHasNext(from)) {
            	downloadBuffer = fts.downloadFile(from);
            	os.write(downloadBuffer);
            }
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //比较文件内容是否一致
        if(md5.equals(CheckMD5.getMd5ByFile(destination))) {
        	System.out.println(destination.getAbsolutePath() + "download successfully.");
        } else {
        	System.out.println(destination.getAbsolutePath() + "下载失败.请重新下载!");
        }
    }


    /**
     * 用于上传文件，包括目录文件
     * @param from 源文件绝对路径
     * @param to 目标文件的绝对路径
     * @throws RemoteException
     */
	public void upload(String from, String to) throws RemoteException {
		// TODO Auto-generated method stub
		//将源文件的目录下所有文件的绝对路径发出
		File source = new File(from);
		String[] filePaths = new FilePathsCreator().getFilePathsOf(source);
		fts.sendFilePaths(filePaths, to);//远端根据发出绝对路径创建目录层次
		
		//依次上传非目录文件
		for(String filepath : filePaths) {
			File file = new File(filepath);
			if(file.isFile()) {
				uploadFile(file);
			}
		}
	}

	/**
	 * 用于上传普通文件
	 * 被upload(String, to)调用
	 * @param file
	 * @throws RemoteException
	 */
	private void uploadFile(File file) throws RemoteException{
		// TODO Auto-generated method stub
		String filepath = file.getAbsolutePath();
		System.out.println(filepath + " is uploading");
		
		fts.uploadFileStart(filepath);
	
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		long fileSize = file.length();
        int lastRead = 0;
        long hasUploaded = 0;
        while(hasUploaded != fileSize) {
        	try {
        		lastRead = is.read(buffer);	//read data
        	} catch(IOException e) {
				e.printStackTrace();
			}
        	
            if (lastRead == BSIZE) {//完整读取一次
                fts.uploadFile(filepath, buffer);
                hasUploaded += lastRead;    
            } else if (lastRead != -1) {//不完整读取一次
            	byte[] tempBuffer = new byte[lastRead];
                System.arraycopy(buffer, 0, tempBuffer, 0, lastRead);
                fts.uploadFile(filepath, tempBuffer);
                hasUploaded += lastRead;  
            } else {
            	break;	//读取为空	
            }   
        }
        
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//判断文件内容是否一致
		String md5 = CheckMD5.getMd5ByFile(file);
		if(!fts.uploadFileEnd(filepath, md5 )) {
			System.out.println(filepath + "上传失败：上传文件内容不一致.请重新上传");
			//throw new RemoteException(filepath + "上传失败：上传文件内容不一致.请重新上传");
		}
	}

}
