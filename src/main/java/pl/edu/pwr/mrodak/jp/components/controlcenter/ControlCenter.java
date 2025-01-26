package pl.edu.pwr.mrodak.jp.components.controlcenter;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;
import pl.edu.pwr.mrodak.jp.components.observer.Observable;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlCenter extends Observable implements IControlCenter {
    private String name;
    private String tailorName;
    private String tailorHost;
    private int tailorPort;

    private Map<IRetensionBasin, String> retensionBasins = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    protected ControlCenter(String controlCenterName, String tailorName, String tailorHost, int tailorPort) throws RemoteException {
        this.name = controlCenterName;
        this.tailorName = tailorName;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException {
        //Check if irb is null or if it is already in the map
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
            IControlCenter ic = (IControlCenter) UnicastRemoteObject.exportObject(this,0);
            Registry registry = LocateRegistry.getRegistry(tailorHost,tailorPort);
            ITailor it = (ITailor) registry.lookup(tailorName);

            if (it.register(ic, name)) {
                System.out.println("Registered with Tailor");
            } else {
                System.out.println("Failed to register with Tailor");
            }
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        monitorBasins();
    }

    public void monitorBasins() {
        scheduler.scheduleAtFixedRate(() -> {
            for(IRetensionBasin irb : retensionBasins.keySet()) {
                try {
                    String fillStatus = String.valueOf(irb.getFillingPercentage());
                    int waterDischarge = irb.getWaterDischarge();
                    notifyObservers(retensionBasins.get(irb), fillStatus, waterDischarge);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 4, TimeUnit.SECONDS);
    }

    public void contactRetensionBasinToSetWaterDischarge(String retensionBasinName, int waterDischarge) {
        try {
            for(IRetensionBasin irb : retensionBasins.keySet()) {
                if(retensionBasins.get(irb).equals(retensionBasinName)) {
                    irb.setWaterDischarge(waterDischarge);
                    System.out.println("Set water discharge for: " + retensionBasinName);
                    return;
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}