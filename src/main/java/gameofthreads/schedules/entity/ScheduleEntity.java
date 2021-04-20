package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @Column(name = "public_link")
    private String publicLink;

    @OneToMany(mappedBy = "schedule")
    private Set<SubscriptionEntity> subscriptions;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ConferenceEntity> conferenceEntities;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL)
    private ExcelEntity excelEntity;

    public ScheduleEntity() {

    }

    public ScheduleEntity(String fileName, String publicLink, ExcelEntity excelEntity) {
        this.fileName = fileName.split("\\.")[0];
        this.publicLink = publicLink;
        this.conferenceEntities = new ArrayList<>();
        this.excelEntity = excelEntity;
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

    public List<ConferenceEntity> getConferences() {
        return conferenceEntities;
    }
}
