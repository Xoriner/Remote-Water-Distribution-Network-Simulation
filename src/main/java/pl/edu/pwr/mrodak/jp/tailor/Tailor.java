package pl.edu.pwr.mrodak.jp.tailor;

import interfaces.IControlCenter;
import interfaces.ITailor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Tailor implements ITailor {
    private String host;
    private int port;
    Map<String,Remote> controlCenterMap = new HashMap<>();

    public Tailor(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected void startTailor() {
        try {
            ITailor it = (ITailor) UnicastRemoteObject.exportObject(this,0);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("Tailor", it);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean register(Remote r, String name) throws RemoteException {
        /*
        // informację na temat tego na jakim hoście i porcie rzeczywiście działa namiastka
        // chyba najłatwiej uzyskać parsując wynik metody toString()
        // metoda ta zwraca ciąg znaków podobny do poniższego:
        // Proxy[IControlCenter,RemoteObjectInvocationHandler[UnicastRef [liveRef: [endpoint:[192.168.1.153:3000](remote),objID:[-50fb9f25:1945c684c6d:-7fff, 7487353482432237380]]]]]
        // wystarczy więc wyciągnąć z niego podciąg korzystając z regexp
        Pattern pattern = Pattern.compile(".*endpoint:\\[(.*)\\]\\(remote.*");
        Matcher matcher = pattern.matcher(r.toString());
        if (matcher.find())
        {
            System.out.println(matcher.group(1)); //
        }
        czyli zamiast name można byłoby użyć wyciągnięty ciąg znaków host:port
        ale to byłoby mało czytelne
        */
        if(r instanceof IControlCenter) {
            if(!controlCenterMap.containsKey(name)) {
                controlCenterMap.put(name,r);
                System.out.println("registration of control center named: " + name);
                ((IControlCenter) r).assignRetensionBasin(null, "nothing");
                return true;
            }
            else return false;
        }

        return false;
    }

    @Override
    public boolean unregister(Remote r) throws RemoteException {
        return false;
    }


}
