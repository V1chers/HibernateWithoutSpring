package ru.aston.event;

import java.util.Objects;

public class NotificationEvent {

    private Integer userId;

    private String name;

    private String email;

    private Action action;

    public NotificationEvent() {
    }

    public NotificationEvent(Integer userId, String name, String email, Action action) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEvent that = (NotificationEvent) o;
        return Objects.equals(userId, that.userId) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, action);
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "action=" + action +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public enum Action {
        CREATED,
        DELETED
    }
}
