package pl.edu.pwr.mrodak.jp;

import interfaces.IControlCenter;
import interfaces.ITailor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tailor implements ITailor {
    Map<String,Remote> ccmap = new HashMap<>();

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
            if(!ccmap.containsKey(name)) {
                ccmap.put(name,r);
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
    public static void main(String[] args) {
        Tailor tailor = new Tailor();
        try {
            ITailor it = (ITailor) UnicastRemoteObject.exportObject(tailor,0);
            Registry r = LocateRegistry.createRegistry(2000);
            r.rebind("Tailor", it);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
