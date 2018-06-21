package operation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import helper.CheckMD5;
import helper.FilePathsCreator;
import helper.FilePathsHandler;

public class FileTransferServiceImpl extends UnicastRemoteObject 
	implements FileTransferService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5766466533685636487L;
	private FilePathsHandler helper = null;
	private Map<String, InputStream> mapOfDownload = null;
	private Map<String, OutputStream> mapOfUpload = null;
	private final int BSIZE = 1024 * 1024;
	private byte[] buffer = new byte[BSIZE];
	private long hasDownloaded = 0;

	public FileTransferServiceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		helper = new FilePathsHandler(this);
		mapOfDownload = new HashMap<String, InputStream>();
		mapOfUpload = new HashMap<String, OutputStream>();
	}

	@Override
	public String[] getFilePaths(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		File file = new File(filename);
		if(!file.exists()) {
			throw new RemoteException("下载文件不存在,请检查文件路径是否正确.");
		}
		
		return new FilePathsCreator().getFilePathsOf(file);
	}

	@Override
	public String downloadFileStart(String filePath) throws RemoteException {
		// TODO Auto-generated method stub
		
		System.out.println(filePath +  " is downloading");
		File downloadFile = new File(filePath);
		
		if(!downloadFile.exists() )
			throw new RemoteException("要下载的文件不存在");
		else if(!downloadFile.isFile() )
			throw new RemoteException("没有该路径的普通文件可下载");
		
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(downloadFile));
			mapOfDownload.put(filePath, in);	//bind InputStream with downloadID
		} catch (IOException e) {
			throw new RemoteException(filePath + "下载失败：服务端输入流打开失败.请重新下载.");
		}
		
		return CheckMD5.getMd5ByFile(downloadFile);
	}

	@Override
	public byte[] downloadFile(String filePath) throws RemoteException {
		// TODO Auto-generated method stub
		
		InputStream in = null;
		if(mapOfDownload.containsKey(filePath))
			in = mapOfDownload.get(filePath);	//get InputStream
		else
			throw new RemoteException("文件路径不存在");
		
		int bytesOfRead = 0;
		try {
            bytesOfRead = in.read(buffer);	//read data
            hasDownloaded += bytesOfRead; 
		} catch (IOException e) {
			throw new RemoteException("文件下载出错:" + filePath);
		}
		
		if(bytesOfRead == BSIZE) {//完整长度读取一次
            return buffer;
        } else {		//不完整长度读取一次	
            byte[] tempBuffer = new byte[bytesOfRead];
            System.arraycopy(buffer, 0, tempBuffer, 0, bytesOfRead);
            return tempBuffer;
        }
		
	}

	@Override
	public boolean downloadFileHasNext(String filePath) throws RemoteException {
		// TODO Auto-generated method stub
		if(hasDownloaded != new File(filePath).length()) {
			return true;
		} else {
			hasDownloaded = 0;	//将hasDownloaded清零
			
			try {	
				InputStream in = mapOfDownload.get(filePath);	//get InputStream
				mapOfDownload.remove(filePath);	//remove downloadID
				in.close();		//close Input stream
			} catch (IOException e) {
				throw new RemoteException("文件下载出错:" + filePath + ":不能正常关闭输出流");
			}
			
			return false;
		}		
	}

	@Override
	public void sendFilePaths(String[] filePaths, String to) throws RemoteException {
		// TODO Auto-generated method stub
        helper.createFiles(filePaths, to);//在destination目录下创建文件
	}
    
	@Override
	public void uploadFileStart(String filePath) throws RemoteException {
		// TODO Auto-generated method stub
		File targetFile = helper.getFileOf(filePath);
		
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(targetFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(filePath +"上传错误：服务端输出流打开出错");
		}
		
		System.out.println(targetFile.getAbsolutePath() + " is uploading...");
		mapOfUpload.put(filePath, out);	//bind OutputStream with filePath
	}

	@Override
	public void uploadFile(String filePath, byte[] data) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			OutputStream out = mapOfUpload.get(filePath);	//get OutputStream
			out.write(data);	//write data
		} catch (IOException e) {
			throw new RemoteException(filePath +"上传错误：服务端写入数据时出错");
		}
	}

	@Override
	public boolean uploadFileEnd(String filePath, String md5) throws RemoteException {
		//关闭输出流
		OutputStream out = mapOfUpload.get(filePath);	//get OutputStream
		mapOfUpload.remove(filePath);	//解除映射关系
		try {
			out.close();	//close output stream
		} catch (IOException e) {
			throw new RemoteException(filePath +"上传错误：服务端输出流关系时出错");
		}
		
		//获得文件对应Md5并检查文件文件内容是否一致
		File file = helper.getFileOf(filePath);
		helper.removeFileOf(filePath); //解除映射关系
		if(CheckMD5.getMd5ByFile(file).equals(md5)) {
			System.out.println(file.getAbsolutePath() + " has uploaded.");
			return true;
		} else {
			System.out.println(file.getAbsolutePath() + "uploading failed:文件内容不一致.");
			return false;
		}	
	}
	
}