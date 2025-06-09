package com.project.spring.pawple.application.point;

// import java.util.Random;

// import org.springframework.stereotype.Service;


// @Service
// public class PointService {
//     private final UserRepository userRepository;
//     private final PointHistoryRepository pointHistoryRepository;
//     private final Random random = new Random();

//     public PointService(UserRepository userRepository, PointHistoryRepository pointHistoryRepository) {
//         this.userRepository = userRepository;
//         this.pointHistoryRepository = pointHistoryRepository;
//     }

//     // 30% 확률로 5~15점 포인트 적립
//     public boolean awardRandomPoints(String username, String reason) {
//         UserEntity user = userRepository.findByName(username)
//             .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
//         if (random.nextInt(100) < 30) { // 30% 확률
//             int points = 5 + random.nextInt(11);
//             user.addPoint(points);
//             userRepository.save(user);
    
//             PointHistory history = new PointHistory();
//             history.setUserId(user.getId());
//             history.setPointAmount(points);
//             history.setReason(reason);
//             pointHistoryRepository.save(history);
            
//             return true;
//         }
//         return false;
//     }
    
// }
