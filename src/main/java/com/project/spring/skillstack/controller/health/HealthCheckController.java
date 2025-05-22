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
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.spring.skillstack.dto.HealthCheckRequest;
import com.project.spring.skillstack.dto.HealthCheckResultResponse;
import com.project.spring.skillstack.service.HealthCheckService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthCheckService service;
    private final List<String> categories = List.of("심장", "위/장", "피부/귀", "신장/방광", "면역력/호흡기", "치아", "뼈/관절", "눈", "행동");

    @GetMapping("/check/{step}")
    public String showStep(@PathVariable int step, Model model) {
        if (step >= categories.size()-1) { return "redirect:/health/result";}

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
                           HttpSession session) {

    Map<String, List<String>> saved = (Map<String, List<String>>) session.getAttribute("healthData");
    if (saved == null) saved = new HashMap<>();
    saved.put(category, answers != null ? answers : List.of("없어요"));
    session.setAttribute("healthData", saved);

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
            case "위/장" -> List.of("구토를 자주 해요", "설사를 자주 해요", "없어요");
            case "피부/귀" -> List.of("피부에서 냄새가 나요","귀에서 분비물이 나와요","피부가 빨개요","가려워서 자주 긁어요","없어요");
            case "신장/방광" -> List.of("소변을 자주 봐요", "소변 냄새가 강해요", "없어요");
            case "면역력/호흡기" -> List.of("기침을 자주 해요", "열이 있어요", "없어요");
            case "치아" -> List.of("입에서 냄새가 나요", "치아가 누렇게 변했어요", "없어요");
            case "뼈/관절" -> List.of("절뚝거려요", "계단을 오르기 힘들어해요", "없어요");
            case "눈" -> List.of("눈꼽이 많이 껴요", "눈이 빨개요", "없어요");
            case "행동" -> List.of("기운이 없어요", "짖는 횟수가 줄었어요", "숨는 일이 많아졌어요", "혼자 있으려고 해요", "없어요");
            default -> List.of("없어요");
        };
    }
}
