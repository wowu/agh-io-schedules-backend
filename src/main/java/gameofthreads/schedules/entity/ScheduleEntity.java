package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ConferenceEntity> conferenceEntities;

    public ScheduleEntity() {

    }

    public ScheduleEntity(String fileName) {
        this.fileName = fileName;
        this.conferenceEntities = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public List<ConferenceEntity> getConferences() {
        return conferenceEntities;
    }
}
