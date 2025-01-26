package pl.edu.pwr.mrodak.jp.tailor;

import pl.edu.pwr.mrodak.jp.components.retensionbasin.RetensionBasin;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IComponentGetter extends Remote {
    Remote findComponent(String name) throws RemoteException;

    boolean assignRiverSectionToRetensionBasin(Remote remoteStub, String componentName, String retensionBasinName) throws RemoteException;

    boolean registerAndAssignRetensionBasinToControlCenter(Remote remoteStub, String componentName, String controlCenterName) throws RemoteException;
    boolean registerAndAssignRiverSectionToEnvironment(Remote remoteStub, String componentName, String environmentName) throws RemoteException;
    boolean assignRetensionBasinToRiverSection(Remote remoteStub, String componentName, String riverName) throws RemoteException;
}