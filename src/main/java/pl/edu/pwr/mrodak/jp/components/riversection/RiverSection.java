package pl.edu.pwr.mrodak.jp.components.riversection;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import pl.edu.pwr.mrodak.jp.components.observer.Observable;
import pl.edu.pwr.mrodak.jp.tailor.IComponentGetter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RiverSection extends Observable implements IRiverSection {
    private String riverSectionName;
    private int delay;

    private String tailorName;
    private String tailorHost;
    private int tailorPort;

    private String environmentName;
    private String inputBasinName;

    private String outputBasinName;
    private IRetensionBasin outputRetensionBasin;

    private ScheduledExecutorService scheduler;

    //get Rainfall from Environment
    private int rainFall = 20;
    private int realDischarge = 0;

    protected RiverSection(String riverSectionName, int riverSectionDelay, String tailorName, String tailorHost, int tailorPort, String environmentName, String inputBasinName) throws RemoteException {
        this.riverSectionName = riverSectionName;
        this.delay = riverSectionDelay;
        this.tailorName = tailorName;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        this.environmentName = environmentName;
        this.inputBasinName = inputBasinName;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startRiverSection() {
        try {
            IRiverSection irs = (IRiverSection) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor it = (ITailor) registry.lookup(tailorName);

            if (((IComponentGetter) it).registerAndAssignRiverSectionToEnvironment(irs, riverSectionName, environmentName)) {
                System.out.println("Registered and assigned with Tailor");
            } else {
                System.out.println("Failed to register with Tailor or assign with Environment");
            }
            monitorOutputRetentionBasin();
            scheduler.scheduleAtFixedRate(this::calculateAndSendWaterInflow, 0, delay, TimeUnit.MILLISECONDS);
            //registerWithInputRetentionBasin();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void calculateAndSendWaterInflow() {
        int waterInflow = calculateWaterInflow();
        sendWaterInflowToOutputBasin(waterInflow);
    }

    private void sendWaterInflowToOutputBasin(int waterInflow) {
        if (outputRetensionBasin != null) {
            try {
                outputRetensionBasin.setWaterInflow(waterInflow, riverSectionName);
                System.out.println("Water inflow sent to output basin: " + waterInflow);
            } catch (RemoteException e) {
                throw new RuntimeException("Failed to send water inflow to output basin", e);
            }
        } else {
            System.err.println("Output Retension Basin is not assigned.");
        }
    }

    private int calculateWaterInflow() {
        return rainFall + realDischarge;
    }

    public void monitorOutputRetentionBasin() {
        scheduler.scheduleAtFixedRate(() -> {
            notifyObservers(outputBasinName, "", 0);
        }, 0, 4, TimeUnit.SECONDS);
    }

    @Override
    public void setRealDischarge(int i) throws RemoteException {
        this.realDischarge = i;
    }

    @Override
    public void setRainfall(int i) throws RemoteException {
        this.rainFall = i;
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin iRetensionBasin, String s) throws RemoteException {
        this.outputRetensionBasin = iRetensionBasin;
        this.outputBasinName = s;
        System.out.println("Assigned retension basin: " + s);
    }
}
