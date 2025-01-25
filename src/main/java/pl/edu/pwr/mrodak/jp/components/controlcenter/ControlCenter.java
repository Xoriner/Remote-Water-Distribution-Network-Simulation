package pl.edu.pwr.mrodak.jp.components.controlcenter;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControlCenter extends UnicastRemoteObject implements IControlCenter {
    private String name;
    private String tailorName;
    private String tailorHost;
    private int tailorPort;

    private Map<IRetensionBasin, String> retensionBasins = new ConcurrentHashMap<>();

    protected ControlCenter(String controlCenterName, String tailorName, String tailorHost, int tailorPort) throws RemoteException {
        this.name = controlCenterName;
        this.tailorName = tailorName;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) {
        //TODO: Check if irb is null or if it is already in the map
        if(irb == null || retensionBasins.containsValue(name)) {
            System.out.println("got null");
        }
        else {
            retensionBasins.put(irb, name);
            System.out.println("Assigned retension basin: " + name);
            System.out.println(irb.toString());
        }
    }

    protected void startControlCenter() {
        try {
            //TODO: Consider using exportObject instead of extending UnicastRemoteObject
            //extends UnicastRemoteObject so it is not necessary to export it
            //IControlCenter ic = (IControlCenter) UnicastRemoteObject.exportObject(controlCenter,0);

            Registry registry = LocateRegistry.getRegistry(tailorHost,tailorPort);
            ITailor it = (ITailor) registry.lookup(tailorName);

            if (it.register(this, name)) {
                System.out.println("Registered with Tailor");
            } else {
                System.out.println("Failed to register with Tailor");
            }

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Implement this method
    public void contactRetensionBasinToSetWaterDischarge(String retensionBasinName, int waterDischarge) {
        try {
            Registry registry = LocateRegistry.getRegistry(tailorHost,tailorPort);
            ITailor it = (ITailor) registry.lookup(tailorName);
            //it.setWaterDischarge(retensionBasinName, waterDischarge);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}