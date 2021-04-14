package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.Excel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelRepository extends JpaRepository<Excel, Integer> {
    @Query("SELECT e.excelName from Excel e")
    List<String> findAllExcelNames();
}
