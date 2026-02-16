package ineventory.Repository;

import ineventory.Entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long>{

    List<ActivityLog> findByUsername(String username);
}
