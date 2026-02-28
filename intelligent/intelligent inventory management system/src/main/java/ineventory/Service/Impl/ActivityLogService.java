package ineventory.Service.Impl;

import ineventory.Entity.ActivityLog;
import ineventory.Entity.User;
import ineventory.Repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public void log(User user, String action){
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(user);
        activityLog.setUsername(user.getUsername());
        activityLog.setAction(action);
        activityLog.setTimestamp(LocalDateTime.now());
        repository.save(activityLog);
//        repository.save(
//                ActivityLog.builder()
//                        .username(username)
//                        .action(action)
//                        .build()
//                );
    }
    public List<ActivityLog> getAllLogs(){
        return repository.findAll();
    }
    public List<ActivityLog> getLogByUsername(String username) {
        return repository.findByUsername(username);
    }
}
