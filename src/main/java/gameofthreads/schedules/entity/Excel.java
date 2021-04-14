package gameofthreads.schedules.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "excel")
public class Excel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "excel_name")
    private String excelName;
    @Column(name = "excel_type")
    private String excelType;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name = "data")
    private byte[] data;

    public Excel() {
    }

    public Excel(String excelName, String excelType, byte[] data) {
        this.excelName = excelName;
        this.excelType = excelType;
        this.data = data;
    }

    public String getExcelName() {
        return excelName;
    }

    public String getExcelType() {
        return excelType;
    }

    public byte[] getData() {
        return data;
    }
}
