package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelRepository extends JpaRepository<ExcelEntity, Integer> {
    @Query("SELECT e.excelName from ExcelEntity e")
    List<String> findAllExcelNames();
}
