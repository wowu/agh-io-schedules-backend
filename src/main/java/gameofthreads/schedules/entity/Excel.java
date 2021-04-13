package gameofthreads.schedules.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "excel")
public class Excel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String excelname;
    private String exceltype;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;

    public Excel() {
    }

    public Excel(String excelName, String excelType, byte[] data) {
        this.excelname = excelName;
        this.exceltype = excelType;
        this.data = data;
    }

    public String getExcelName() {
        return excelname;
    }

    public String getExcelType() {
        return exceltype;
    }

    public byte[] getData() {
        return data;
    }
}
