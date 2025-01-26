package pl.edu.pwr.mrodak.jp.components.environment;

import interfaces.IEnvironment;
import interfaces.IRiverSection;
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

public class Environment extends Observable implements IEnvironment {
    private String name;
    private String tailorName;
    private String tailorHost;
    private int tailorPort;

    private Map<IRiverSection, String> riverSections = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    protected Environment(String environmentName, String tailorName, String tailorHost, int tailorPort) throws RemoteException {
        this.name = environmentName;
        this.tailorName = tailorName;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void assignRiverSection(IRiverSection iRiverSection, String s) throws RemoteException {
        if (iRiverSection == null || riverSections.containsValue(s)) {
            System.out.println("got null");
        } else {
            riverSections.put(iRiverSection, s);
            System.out.println("Assigned river section: " + s);
            System.out.println(iRiverSection.toString());
        }
    }

    protected void startEnvironment() {
        try {
            IEnvironment ie = (IEnvironment) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor it = (ITailor) registry.lookup(tailorName);

            if (it.register(ie, name)) {
                System.out.println("Registered with Tailor");
            } else {
                System.out.println("Failed to register with Tailor");
            }

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        monitorRiverSections();
    }

    public void monitorRiverSections() {
        scheduler.scheduleAtFixedRate(() -> {
            for(IRiverSection irs : riverSections.keySet()) {
                notifyObservers(riverSections.get(irs), "", 0);
            }
        },0, 4, TimeUnit.SECONDS);
    }

    public void contactRiverSectionToSetRainfall(String riverSectionName, int rainfall) {
        try {
            for(IRiverSection irs : riverSections.keySet()) {
                if(riverSections.get(irs).equals(riverSectionName)) {
                    irs.setRainfall(rainfall);
                    System.out.println(new StringBuilder().append("Set rain fall for: ").append(riverSectionName).toString());
                    return;
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
