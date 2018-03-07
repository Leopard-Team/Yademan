package ir.fekrafarinan.yademman.Leitner.Models;

/**
 * Created by admin on 7/24/2017.
 */

public class QueueObject {
    int id;
    String work;
    int localId,serverId;


    public QueueObject(int id, String work, int localId, int serverId) {
        this.serverId = serverId;
        this.id = id;
        this.work = work;
        this.localId = localId;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

}
