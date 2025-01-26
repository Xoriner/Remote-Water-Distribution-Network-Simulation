package pl.edu.pwr.mrodak.jp.components.observer;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String name, String stringInfo, int infInfo) {
        for (Observer observer : observers) {
            observer.update(name, stringInfo, infInfo);
        }
    }
}