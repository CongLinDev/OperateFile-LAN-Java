package operation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileTransferService extends Remote {
	/**
	 *用于获得要下载的目录下所有文件的绝对路径(如果要下载的是非目录文件只发送它本身的绝对路径)
	 * @param filename 下载文件路径
	 * @return
	 * @throws RemoteException
	 */
	String[] getFilePaths(String filename) throws RemoteException;
	/**
	 * 用于做开始下载非目录文件前的处理
	 * 打开对应输入流等
	 * @param filePath 下载文件的绝对路径
	 * @return 返回要下载文件的MD5码
	 * @throws RemoteException
	 */
	String downloadFileStart(String filePath) throws RemoteException;
	/**
	 * 下载非目录文件
	 * @param filePath 下载文件的绝对路径
	 * @return 返回文件内容的一部分的字节数组
	 * @throws RemoteException
	 */
	byte[] downloadFile(String filePath) throws RemoteException;
	/**
	 * 用于判断下载文件是否完成
	 * @param filePath 下载文件的绝对路径
	 * @return 
	 * @throws RemoteException
	 */
	boolean downloadFileHasNext(String filePath) throws RemoteException;
	
	/**
	 * 用于发送上传的目录下所有文件的绝对路径(如果是非目录文件只发送它本身的绝对路径)
	 * @param filePaths
	 * @param to 表示上传的目的路径
	 * @throws RemoteException
	 */
	void sendFilePaths(String[] filePaths, String to) throws RemoteException;
	/**
	 * 用于做开时上传非目录文件前的处理
	 * @param filePath 上传文件名(绝对路径)
	 * @throws RemoteException
	 */
	void uploadFileStart(String filePath) throws RemoteException;
	/**
	 * 用于发送正在上传非目录文件的数据
	 * @param filePath 上传文件名(绝对路径)
	 * @param data 上传文件的一部分数据
	 * @throws RemoteException
	 */
	void uploadFile(String filePath, byte[] data) throws RemoteException;
	/**
	 * 用于做上传非目录文件结束后的处理
	 * @param filePath
	 * @param md5
	 * @return 判断上传文件内容是否和源文件一致
	 * @throws RemoteException
	 */
	boolean uploadFileEnd(String filePath, String md5) throws RemoteException;
}