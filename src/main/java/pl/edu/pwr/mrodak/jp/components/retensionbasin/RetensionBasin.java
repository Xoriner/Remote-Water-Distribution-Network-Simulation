package pl.edu.pwr.mrodak.jp.components.retensionbasin;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import pl.edu.pwr.mrodak.jp.components.observer.Observable;
import pl.edu.pwr.mrodak.jp.tailor.IComponentGetter;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

public class RetensionBasin extends Observable implements IRetensionBasin {
    private int maxVolume;

    private String tailorName;
    private String tailorHost;
    private int tailorPort;

    private String retensionBasinName;
    private String controlCenterName;
    private int currentVolume;
    private int waterDischarge = 10;

    private ScheduledExecutorService scheduler;

    private List<String> incomingRiverSectionName = new ArrayList<String>();
    private String outgoingRiverSectionName;
    private ConcurrentMap<Integer, Integer> inflows = new ConcurrentHashMap<>();

    protected RetensionBasin(String name, String tailorName, String tailorHost, int tailorPort, int maxVolume, String controlCenterName) throws RemoteException {
        this.retensionBasinName = name;
        this.tailorName = tailorName;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        this.maxVolume = maxVolume;
        this.controlCenterName = controlCenterName;
    }

    //getWaterDischarge called by ControlCenter
    @Override
    public int getWaterDischarge() throws RemoteException {
        return waterDischarge;
    }

    //getFillingPercentage called by ControlCenter
    @Override
    public long getFillingPercentage() throws RemoteException {
        return (currentVolume * 100) / maxVolume;
    }

    //setWaterDischarge called by ControlCenter
    @Override
    public void setWaterDischarge(int i) throws RemoteException {
        this.waterDischarge = i;
    }

    //setWaterInflow called by RiverSection
    @Override
    public void setWaterInflow(int i, String s) throws RemoteException {

    }

    //output RiverSection
    @Override
    public void assignRiverSection(IRiverSection iRiverSection, String s) throws RemoteException {

    }

    public void startRetensionBasin() {
        try {
            IRetensionBasin irb = (IRetensionBasin) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor it = (ITailor) registry.lookup(tailorName);

            if (((IComponentGetter) it).registerAndAssignRetensionBasinToControlCenter(irb, retensionBasinName, controlCenterName)) {
                System.out.println("Registered and assigned with Tailor");
            } else {
                System.out.println("Failed to register and assign with Tailor");
            }

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addIncomingRiverSection(String riverName) {
        if(incomingRiverSectionName.contains(riverName)) {
            System.out.println("River " + riverName + " already exists");
            return;
        }
        incomingRiverSectionName.add(riverName);
        System.out.println("Added IncomingRiverSection: " + riverName);
    }

    public void assignToIncomingRiverSections() {
        for(String riverName : incomingRiverSectionName) {
            try {
                Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
                ITailor it = (ITailor) registry.lookup(tailorName);

                if (((IComponentGetter) it).assignRetensionBasinToRiverSection(this, retensionBasinName, riverName)) {
                    System.out.println("Registered and assigned with Tailor");
                } else {
                    System.out.println("Failed to register and assign with Tailor");
                }

            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
