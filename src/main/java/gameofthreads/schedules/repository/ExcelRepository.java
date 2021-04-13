package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.Excel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcelRepository extends JpaRepository<Excel, Integer> {
    Optional<Excel> findByExcelName(String excelName);
}
