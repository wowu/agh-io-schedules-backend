package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @Column(name = "description")
    private String description;

    @Column(name = "public_link")
    private String publicLink;

    @Column(name = "notifications")
    private Boolean notifications;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private Set<SubscriptionEntity> subscriptions;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private Set<ConferenceEntity> conferenceEntities;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL)
    private ExcelEntity excelEntity;

    public ScheduleEntity() {

    }

    public ScheduleEntity(String fileName, String publicLink, ExcelEntity excelEntity, Boolean notifications) {
        this.fileName = fileName.split("\\.")[0];
        this.description = "";
        this.publicLink = publicLink;
        this.conferenceEntities = new HashSet<>();
        this.excelEntity = excelEntity;
        this.notifications = notifications;
    }

    public Integer getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPublicLink() {
        return publicLink;
    }

    public Set<ConferenceEntity> getConferences() {
        return conferenceEntities;
    }

    public Set<SubscriptionEntity> getSubscriptions() {
        return subscriptions;
    }

    public String getDescription() {
        return description;
    }

    public ExcelEntity getExcelEntity() {
        return excelEntity;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setConferenceEntities(Set<ConferenceEntity> conferenceEntities) {
        this.conferenceEntities = conferenceEntities;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }
}
