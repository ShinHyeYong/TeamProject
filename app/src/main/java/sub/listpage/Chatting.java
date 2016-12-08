package sub.listpage;

/**
 * Created by HY on 2016-12-08.
 */

public class Chatting {
    private String id;
    private String comment;
    private String time;
    private int res;

    public Chatting(String id, String comment, String time, int res) {
        this.id = id;
        this.comment = comment;
        this.time = time;
        this.res = res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
