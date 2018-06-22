package client;

import operation.FileTransferService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class RmiClient {
    private static FileTransferHandler handler;
    //private static long fileSize = 0;

    /**
     * 用于初始化客户端远端对象的函数
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    private static void fileTransferServiceInit() throws MalformedURLException, RemoteException, NotBoundException {
        String rmi = "rmi://192.168.1.2:5000/FileTransfer";
        FileTransferService fts = (FileTransferService)Naming.lookup(rmi);

        handler = new FileTransferHandler(fts);
    }

    /**
     * 处理指令
     * @param args
     * @throws Exception
     */
    public static void commandProcressor(String[] args) throws Exception{
        if( args.length != 3 && args.length != 2)
            throw new Exception("命令行参数错误");

        String command = args[0];
        String from = args[1];
        String to = null;

        if(args.length == 3)
            to = args[2];
        else
            to = from;

        switch(command) {
            case "upload":
                handler.upload(from, to);
                System.out.println("上传完成!");
                break;
            case "download":
                handler.download(from, to);
                System.out.println("下载完成!");
                break;
            default:
                System.out.println("command not found : "+command);
                break;
        }
    }
    
    /**
     * 主函数
     * @param args
     */
    public static void main(String args[]) {
        try {
            fileTransferServiceInit();
            commandProcressor(args);
        }catch (NotBoundException e) {
            e.printStackTrace();
        }catch (RemoteException e) {
            e.printStackTrace();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
