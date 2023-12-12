package DBConnectControlDB;

import java.sql.Timestamp;

public class DataCheckpoint {
    private int id;
    private String groupName;
    private String name;
    private String code;
    private Timestamp dataUptoDate;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public DataCheckpoint() {
    }

    public DataCheckpoint(int id, String groupName, String name, String code, Timestamp dataUptoDate, String note, Timestamp createdAt, Timestamp updatedAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.groupName = groupName;
        this.name = name;
        this.code = code;
        this.dataUptoDate = dataUptoDate;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Timestamp getDataUptoDate() {
        return dataUptoDate;
    }

    public void setDataUptoDate(Timestamp dataUptoDate) {
        this.dataUptoDate = dataUptoDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "DataCheckpoint{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", dataUptoDate=" + dataUptoDate +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                '}';
    }
}
