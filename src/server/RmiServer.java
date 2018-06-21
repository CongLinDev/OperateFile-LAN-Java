package server;

import operation.FileTransferService;
import operation.FileTransferServiceImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiServer {
    //private static String shareDirName = "D:/share";
    //private static File shareDir;
    //public static String shareDirName() { return shareDirName; }

    private static void fileTransferServiceInit() throws RemoteException, MalformedURLException {
		/*
		try {
			shareDir = new File(shareDirName);
			if(!shareDir.exists()) {
				shareDir.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
        FileTransferService fts = new FileTransferServiceImpl();
        String rmi = "//localhost:5000/FileTransfer";
        LocateRegistry.createRegistry(5000);
        Naming.rebind(rmi,fts);
    }

    public static void main(String args[]) {
        try {
            fileTransferServiceInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}