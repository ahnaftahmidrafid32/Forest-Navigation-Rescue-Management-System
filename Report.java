public class Report {
    private String reportId;
    private long timestamp;
    private int hazardCount;
    private int survivorCount;
    private String content;

    public Report(String reportId, int hazardCount, int survivorCount, String content) {
        this.reportId = reportId;
        this.timestamp = System.currentTimeMillis();
        this.hazardCount = hazardCount;
        this.survivorCount = survivorCount;
        this.content = content;
    }

    public String export() {
        return "=== REPORT [" + reportId + "] ===\n" +
                "Timestamp: " + timestamp + "\n" +
                "Hazards: " + hazardCount + "\n" +
                "Survivors: " + survivorCount + "\n" +
                "Content:\n" + content;
    }

    public String getSummary() {
        return "Report[" + reportId + "] | Hazards: " + hazardCount + " | Survivors: " + survivorCount;
    }

    public String getReportId() {return reportId;}

    public long getTimestamp() {return timestamp; }

    public int getHazardCount() { return hazardCount;}

    public int getSurvivorCount() {return survivorCount; }

    public String getContent() {return content; }
}

