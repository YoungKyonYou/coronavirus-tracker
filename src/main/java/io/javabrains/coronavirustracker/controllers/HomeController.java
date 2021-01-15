package io.javabrains.coronavirustracker.controllers;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;
    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats=coronaVirusDataService.getAllStats();
        int totalReportedCases=allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        //객체의 리스트를 스트림으로 변환한 다음 int로 mapping하고 각각의 오브젝트 stat가 integer 값인 getLatestTotalCases 값들을 sum할 것임
        int totalNewCases=allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);

        return "home";
    }
}
