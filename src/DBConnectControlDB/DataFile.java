package DBConnectControlDB;

import java.sql.Timestamp;

public class DataFile {
    private int id;
    private String name;
    private Long rowCount;
    private Integer dfConfigId;
    private String status;
    private Timestamp fileTimestamp;
    private Timestamp dataRangeFrom;
    private Timestamp dataRangeTo;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean isInserted;
    private Timestamp deletedAt;

    public DataFile() {
    }

    public DataFile(int id, String name, Long rowCount, Integer dfConfigId, String status, Timestamp fileTimestamp, Timestamp dataRangeFrom, Timestamp dataRangeTo, String note, Timestamp createdAt, Timestamp updatedAt, Integer createdBy, Integer updatedBy, Boolean isInserted, Timestamp deletedAt) {
        this.id = id;
        this.name = name;
        this.rowCount = rowCount;
        this.dfConfigId = dfConfigId;
        this.status = status;
        this.fileTimestamp = fileTimestamp;
        this.dataRangeFrom = dataRangeFrom;
        this.dataRangeTo = dataRangeTo;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.isInserted = isInserted;
        this.deletedAt = deletedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getDfConfigId() {
        return dfConfigId;
    }

    public void setDfConfigId(Integer dfConfigId) {
        this.dfConfigId = dfConfigId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getFileTimestamp() {
        return fileTimestamp;
    }

    public void setFileTimestamp(Timestamp fileTimestamp) {
        this.fileTimestamp = fileTimestamp;
    }

    public Timestamp getDataRangeFrom() {
        return dataRangeFrom;
    }

    public void setDataRangeFrom(Timestamp dataRangeFrom) {
        this.dataRangeFrom = dataRangeFrom;
    }

    public Timestamp getDataRangeTo() {
        return dataRangeTo;
    }

    public void setDataRangeTo(Timestamp dataRangeTo) {
        this.dataRangeTo = dataRangeTo;
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

    public Boolean getInserted() {
        return isInserted;
    }

    public void setInserted(Boolean inserted) {
        isInserted = inserted;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "DataFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rowCount=" + rowCount +
                ", dfConfigId=" + dfConfigId +
                ", status='" + status + '\'' +
                ", fileTimestamp=" + fileTimestamp +
                ", dataRangeFrom=" + dataRangeFrom +
                ", dataRangeTo=" + dataRangeTo +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", isInserted=" + isInserted +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
