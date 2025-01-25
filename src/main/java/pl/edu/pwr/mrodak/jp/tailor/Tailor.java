package pl.edu.pwr.mrodak.jp.tailor;

import interfaces.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Tailor implements ITailor {
    private String name;
    private String host;
    private int port;

    //map for general remote use of component
    private Map<String, Remote> componentMap = new HashMap<>();

    //maps for specific use of components
    private Map<String, IControlCenter> controlCenterMap = new HashMap<>();
    private Map<String, IEnvironment> environmentMap = new HashMap<>();
    private Map<String, IRetensionBasin> retensionBasinMap = new HashMap<>();
    private Map<String, IRiverSection> riverSectionMap = new HashMap<>();

    public Tailor(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    protected void startTailor() {
        try {
            // Create remote object and export it to receive incoming method calls
            ITailor it = (ITailor) UnicastRemoteObject.exportObject(this,0);

            // Create Registry on port
            Registry registry = LocateRegistry.createRegistry(port);

            // Bind the remote object's stub in the registry
            registry.rebind(name, it);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Remote findComponent(String name) throws RemoteException {
        return componentMap.get(name);
    }

    @Override
    public boolean register(Remote remoteStub, String name) throws RemoteException {
        /*
        // informację na temat tego na jakim hoście i porcie rzeczywiście działa namiastka
        // chyba najłatwiej uzyskać parsując wynik metody toString()
        // metoda ta zwraca ciąg znaków podobny do poniższego:
        // Proxy[IControlCenter,RemoteObjectInvocationHandler[UnicastRef [liveRef: [endpoint:[192.168.1.153:3000](remote),objID:[-50fb9f25:1945c684c6d:-7fff, 7487353482432237380]]]]]
        // wystarczy więc wyciągnąć z niego podciąg korzystając z regexp
        Pattern pattern = Pattern.compile(".*endpoint:\\[(.*)\\]\\(remote.*");
        Matcher matcher = pattern.matcher(remoteStub.toString());
        if (matcher.find())
        {
            System.out.println(matcher.group(1)); //
        }
        czyli zamiast name można byłoby użyć wyciągnięty ciąg znaków host:port
        ale to byłoby mało czytelne
        */
        if (componentMap.containsKey(name)) {
            System.out.println(new StringBuilder().append("duplicate name: ").append(name));
            return false; // Avoid duplicate names
        }

        // specific registration
        switch (remoteStub) {
            case IControlCenter controlCenter -> controlCenterMap.put(name, controlCenter);
            case IEnvironment environment -> environmentMap.put(name, environment);
            case IRetensionBasin retensionBasin -> retensionBasinMap.put(name, retensionBasin);
            case IRiverSection riverSection -> riverSectionMap.put(name, riverSection);
            case null, default -> {
                System.out.println(new StringBuilder().append("unrecognized type: ").append(name));
                return false; // Unrecognized type
            }
        }

        // general registration
        componentMap.put(name, remoteStub);

        /*if(remoteStub instanceof IControlCenter) {
            controlCenterMap.put(name, (IControlCenter) remoteStub);
            System.out.println("registration of control center named: " + name);
            //Szycie połączenia, musi być gdzie indziej
            ((IControlCenter) remoteStub).assignRetensionBasin(null, "nothing");
            return true;
        }*/

        System.out.println(new StringBuilder().append("registration of ").append(name).append(" successful"));
        return true;
    }

    //TODO: Implement this method
    @Override
    public boolean unregister(Remote r) throws RemoteException {
        return false;
    }

    public Map<String, Remote> getComponentMap() {
        return componentMap;
    }
}
