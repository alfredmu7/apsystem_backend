package legacy.cctv_general_automation_report.excel;

import legacy.cctv_general_automation_report.excel.SelectedIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectedIdRepository
        extends JpaRepository<SelectedIdEntity, Long> {
}
