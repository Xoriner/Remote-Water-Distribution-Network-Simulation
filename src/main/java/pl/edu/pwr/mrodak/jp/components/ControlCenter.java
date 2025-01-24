package pl.edu.pwr.mrodak.jp.components;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;

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
            IControlCenter controlCenter = new ControlCenter();
            //IControlCenter ic = (IControlCenter) UnicastRemoteObject.exportObject(controlCenter,0);
            Registry registry = LocateRegistry.getRegistry("192.168.10.156",2000);
            ITailor it = (ITailor) registry.lookup("Tailor");
            it.register(controlCenter,"ControlCenter1");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

    }
}