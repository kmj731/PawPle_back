package com.project.spring.skillstack.controller.health;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.spring.skillstack.dto.HealthCheckRequest;
import com.project.spring.skillstack.dto.HealthCheckResultResponse;
import com.project.spring.skillstack.service.HealthCheckService;
import com.project.spring.skillstack.service.PetService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthCheckController {

    private final PetService petService;  // ✅ 이 줄 추가!
    private final HealthCheckService service;
    private final List<String> categories = List.of("심장", "위/장", "피부/귀", "신장/방광", "면역력/호흡기", "치아", "뼈/관절", "눈", "행동","체중 및 비만도");

    @GetMapping("/check/{step}")
    public String showStep(@PathVariable int step, Model model) {
        if (step >= categories.size()) {
            return "redirect:/health/result";
        }

        model.addAttribute("category", categories.get(step));
        model.addAttribute("step", step);
        model.addAttribute("progress", (step + 1) * 100 / categories.size());
        model.addAttribute("options", getOptionsFor(categories.get(step)));
        return "health_step";
    }

    @PostMapping("/next")
    public String saveAndNext(@RequestParam String category,
                           @RequestParam(required = false) List<String> answers,
                           @SessionAttribute(value = "userId", required = false) Long userId,
                           @RequestParam int step,
                           @RequestParam Long petId, // ✨ petId도 같이 받기
                           HttpSession session) {

    Map<String, List<String>> saved = (Map<String, List<String>>) session.getAttribute("healthData");
    if (saved == null) saved = new HashMap<>();
    saved.put(category, answers != null ? answers : List.of("없어요"));
    session.setAttribute("healthData", saved);

    // ✨ 여기 추가!! petId를 세션에 저장
    session.setAttribute("petId", petId);


    if (step >= categories.size() - 1) {
        return "redirect:/health/result";  // ✅ 정확한 주소
    }

    return "redirect:/health/check/" + (step + 1);
}



@GetMapping("/result")
public String showResult(HttpSession session, Model model) {
    System.out.println("✅ /health/result 컨트롤러 진입 성공");

    
    // 세션에 저장된 건강 체크 데이터 꺼내기
    Map<String, List<String>> saved = (Map<String, List<String>>) session.getAttribute("healthData");

     // 2️⃣ ✨ 여기에 추가: petId를 세션에서 꺼낸다
    Long petId = (Long) session.getAttribute("petId");

    if (petId != null) {
        long dday = petService.getPetCheckupDday(petId); // ✅ 오류 해결됨!
        model.addAttribute("dday", dday);                // 💬 뷰로 넘겨서 표시
    } else {
        System.out.println("❗ petId가 세션에 없어요!");
        model.addAttribute("dday", null);
    }

    // 추가 코드: 2개 이상 체크한 항목만 추출
    Map<String, List<String>> needsAttention = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : saved.entrySet()) {
        List<String> answers = entry.getValue();
    // '없어요'만 체크된 경우 제외 + 2개 이상 선택된 경우만 필터링
    long validCount = answers.stream().filter(answer -> !"없어요".equals(answer)).count();
        if (validCount >= 2) {
            needsAttention.put(entry.getKey(), answers);
        }
    }
    model.addAttribute("needsAttention", needsAttention); // 👉 결과 템플릿에서 사용할 데이터

    // 세션에 데이터가 없다면 처음으로 리다이렉트
    if (saved == null || saved.isEmpty()) {
        System.out.println("❌ 세션에 healthData 없음 → /health/check/0으로 리다이렉트");
        return "redirect:/health/check/0";
    }

    try {
        // 요청 객체 구성
        HealthCheckRequest request = new HealthCheckRequest();
        request.setUserId(1L); // 나중에 로그인 연동되면 세션에서 꺼내기
        request.setSelectedOptions(saved);

        // 서비스 처리
        service.processCheck(request);
        HealthCheckResultResponse result = service.getResult(1L);

        // 결과 템플릿에 데이터 전달
        model.addAttribute("result", result);
        model.addAttribute("selected", saved);

        System.out.println("✅ 결과 처리 성공! 점수: " + result.getScore());
    } catch (Exception e) {
        System.out.println("❌ 결과 처리 중 예외 발생: " + e.getMessage());
        e.printStackTrace();
        return "redirect:/health/check/0";
    }

    return "health_result";
}



    private List<String> getOptionsFor(String category) {
        return switch (category) {
            case "심장" -> List.of("심장박동이 불규칙해요","숨이 가빠요","기절한 적이 있어요","쉽게 지쳐요","없어요");
            case "위/장" -> List.of("구토를 자주 해요", "설사를 자주 해요","밥을 잘 안 먹거나 식욕이 줄었어요", "변 상태가 자주 물처럼 묽어요", "없어요");
            case "피부/귀" -> List.of("피부에서 냄새가 나요","귀에서 분비물이 나와요","피부가 빨개요","가려워서 자주 긁어요","없어요");
            case "신장/방광" -> List.of("소변을 자주 봐요", "소변 냄새가 강해요", "소변을 볼 때 힘들어하거나 자주 실수해요", "소변 색이 평소보다 진하거나 붉어요", "없어요");
            case "면역력/호흡기" -> List.of("기침을 자주 해요","콧물이 나고 코를 자주 문질러요","열이 있어요","숨이 차서 헐떡거려요","없어요");
            case "치아" -> List.of("입에서 냄새가 나요", "딱딱한 사료를 잘 못 씹어요","이가 흔들리거나 빠졌어요", "잇몸이 붓고 피가 나요", "없어요");
            case "뼈/관절" -> List.of("다리를 절뚝거려요","계단을 오르기 힘들어해요","일어나기 힘들어해요", "산책을 싫어해요","없어요");
            case "눈" -> List.of("눈꼽이 많이 껴요","눈이 빨개요","빛에 민감하게 반응해요", "눈이 뿌옇게 보여요","없어요");
            case "행동" -> List.of("기운이 없어요", "짖는 횟수가 줄었어요", "숨는 일이 많아졌어요", "혼자 있으려고 해요", "없어요");
            case "체중 및 비만도" -> List.of("최근 강아지의 체중이 눈에 띄게 늘었거나 줄었어요","허리 라인이 잘 안 보이거나 만져지지 않아요", "배를 만졌을 때 갈비뼈가 잘 느껴지지 않아요", "예전보다 덜 움직이고, 활동량이 줄었거나 쉽게 지쳐해요","없어요");
            default -> List.of("없어요");
        };
    }

    @GetMapping("/dday/{petId}")
    @ResponseBody
    public String getDday(@PathVariable Long petId) {
    long dday = petService.getPetCheckupDday(petId); // ✅ 객체로 호출

    if (dday > 0) {
        return "다음 건강검진까지 D-" + dday;
    } else if (dday == 0) {
        return "오늘 건강검진일이에요!";
    } else {
        return "건강검진이 " + Math.abs(dday) + "일 지났어요. 검진이 필요해요!";
    }
}

}
