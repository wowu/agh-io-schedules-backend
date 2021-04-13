package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.Excel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcelRepository extends JpaRepository<Excel, Integer> {
    @Query("SELECT a FROM Excel a where a.excelname=:excelname")
    Optional<Excel> findByExcelName(String excelname);
}
