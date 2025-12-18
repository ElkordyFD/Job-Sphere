package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple NotificationService using callback approach instead of Observer
 * pattern.
 * Notifications are stored and can be retrieved by UI components.
 */
public class NotificationService {
    private List<String> notifications = new ArrayList<>();
    private List<Consumer<String>> listeners = new ArrayList<>();

    /**
     * Add a listener to receive new notifications.
     */
    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    /**
     * Send a notification to all listeners.
     */
    public void sendNotification(String message) {
        notifications.add(message);
        for (Consumer<String> listener : listeners) {
            listener.accept(message);
        }
    }

    /**
     * Get all notifications.
     */
    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Clear all notifications.
     */
    public void clearNotifications() {
        notifications.clear();
    }
}
