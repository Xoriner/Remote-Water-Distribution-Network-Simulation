package pl.edu.pwr.mrodak.jp;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ControlCenter extends UnicastRemoteObject implements IControlCenter {
    protected ControlCenter() throws RemoteException {
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) {
        if(irb == null)
            System.out.println("got null");
        else
            System.out.println(irb.toString());
    }

    public static void main(String[] args) {
        try {
            IControlCenter cc = new ControlCenter();
            InetAddress inetAddress = InetAddress.getLocalHost();

            // Display the hostname and IP address
            System.out.println("Hostname: " + inetAddress.getHostName());
            System.out.println("IP Address: " + inetAddress.getHostAddress());
            //IControlCenter ic = (IControlCenter) UnicastRemoteObject.exportObject(cc,0);
            Registry registry = LocateRegistry.getRegistry("localhost",2000);
            ITailor it = (ITailor) registry.lookup("Tailor");
            //it.register(ic, "Kontroler1");
            it.register(cc,"Kontroler1");
        } catch (RemoteException | NotBoundException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }
}