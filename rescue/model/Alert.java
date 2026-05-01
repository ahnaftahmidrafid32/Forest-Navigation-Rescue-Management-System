package rescue.model;

public class Alert {
    private String alertID;
    private String message;
    private Priority priority;
    private long timestamp;
    protected boolean isActive;

    public Alert(String alert, String message, Priority priority) {
        this.alertID = alert;
        this.message = message;
        this.priority = priority;
        this.timestamp = System.currentTimeMillis();
        this.isActive = true;
    }

    public void dismiss() { this.isActive = false; }
    public Priority getPriority() { return priority; }

    public String getAlertID() {return alertID; }
    public String getAlertId() { return alertID; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean IsActive() { return isActive; }
    public boolean isActive() { return isActive; }

    public String toString() {
        return "Alert["+alertID+"] ["+priority+"] "+message+" | Active : "+isActive;
    }
}
