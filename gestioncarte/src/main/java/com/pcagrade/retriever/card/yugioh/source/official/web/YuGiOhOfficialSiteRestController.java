package com.pcagrade.retriever.card.yugioh.source.official.web;

import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieDTO;
import com.pcagrade.retriever.card.yugioh.source.official.YuGiOhOfficialSiteService;
import com.pcagrade.retriever.card.yugioh.web.YuGiOhCardRestController;
import com.pcagrade.mason.localization.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/" + YuGiOhOfficialSiteRestController.BASE_PATH)
public class YuGiOhOfficialSiteRestController {


    public static final String BASE_PATH = YuGiOhCardRestController.BASE_PATH + "/official-site";

    @Autowired
    private YuGiOhOfficialSiteService yuGiOhOfficialSiteService;

    @GetMapping("/sets/{pid}/{localization}/name")
    public String getOfficialSiteSetNameWithPid(@PathVariable String pid, @PathVariable Localization localization) {
        return yuGiOhOfficialSiteService.getOfficialSiteSetNameWithPid(pid, localization);
    }

    @GetMapping("/series/{pid}/{localization}")
    public Optional<YuGiOhSerieDTO> getNewSerieByPid(@PathVariable String pid, @PathVariable Localization localization) {
        return yuGiOhOfficialSiteService.getSerieWithPid(pid, localization);
    }
}
