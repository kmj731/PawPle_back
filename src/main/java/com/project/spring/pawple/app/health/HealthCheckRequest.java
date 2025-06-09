
package com.project.spring.pawple.app.health;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class HealthCheckRequest {
    private Long userId;
    private Long petId; // 추가됨
    private Map<String, List<String>> selectedOptions;
    private Map<String, Integer> answers;
}
