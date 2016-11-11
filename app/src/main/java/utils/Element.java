package utils;

/**
 * Created by PSJ on 2016. 11. 1..
 */

public class Element {
    private String title;
    private String timeStamp;

    public Element(String title, String timeStamp) {
        this.title = title;
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
