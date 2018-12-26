package utils.metrics;

public class BrowserConsoleLogEntry {

    private String type;
    private String level;
    private String message;

    public BrowserConsoleLogEntry(String type, String level, String message) {
        this.type = type;
        this.level = level;
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

