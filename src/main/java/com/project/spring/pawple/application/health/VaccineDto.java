package com.project.spring.pawple.application.health;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

// 선택용 (셀렉트박스)
public class VaccineDto {
    private int step; // 백신 단계
    private String name; // 백신 이름
    private String guideMessage; // 사용자에게 보여줄 안내 메시지
    private String intervalType; // "2주", "1년", "6개월" 등 (표시용)
}
