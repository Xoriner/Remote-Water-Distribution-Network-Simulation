package pl.edu.pwr.mrodak.jp.components.retensionbasin;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RetensionBasin extends UnicastRemoteObject implements IRetensionBasin {
    protected RetensionBasin() throws RemoteException {
    }

    //getWaterDischarge called by ControlCenter
    @Override
    public int getWaterDischarge() throws RemoteException {
        return 0;
    }

    //getFillingPercentage called by ControlCenter
    @Override
    public long getFillingPercentage() throws RemoteException {
        return 0;
    }

    //setWaterDischarge called by ControlCenter
    @Override
    public void setWaterDischarge(int i) throws RemoteException {

    }

    //setWaterInflow called by RiverSection
    @Override
    public void setWaterInflow(int i, String s) throws RemoteException {

    }

    //output RiverSection
    @Override
    public void assignRiverSection(IRiverSection iRiverSection, String s) throws RemoteException {

    }
}
