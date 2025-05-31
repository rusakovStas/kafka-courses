package ydx.practicum.model;

public class Message {
    private String payload;
    private String subject;

    public Message() {
        this.payload = "paylod";
        this.subject = "login";
    }

    @Override
    public String toString() {
        return "Message{" +
                "payload='" + payload + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
