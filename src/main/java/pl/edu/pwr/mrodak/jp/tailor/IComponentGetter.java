package pl.edu.pwr.mrodak.jp.tailor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IComponentGetter extends Remote {
    Remote findComponent(String name) throws RemoteException;
    boolean registerAndAssign(Remote remoteStub, String componentName, String controlCenterName) throws RemoteException;
}