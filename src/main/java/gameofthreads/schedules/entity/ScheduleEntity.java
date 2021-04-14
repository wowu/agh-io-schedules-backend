package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @OneToMany(mappedBy = "schedule")
    private List<ConferenceEntity> conferenceEntities;

    public ScheduleEntity() {

    }

    public ScheduleEntity(String fileName) {
        this.fileName = fileName;
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
