package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern: Subject that notifies observers.
 */
public class NotificationService {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
