package com.pcagrade.retriever.card.yugioh.set.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetService;
import com.pcagrade.retriever.card.yugioh.web.YuGiOhCardRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + YuGiOhSetRestController.BASE_PATH)
public class YuGiOhSetRestController {

    public static final String BASE_PATH = YuGiOhCardRestController.BASE_PATH + "/sets";

    @Autowired
    private YuGiOhSetService yuGiOhSetService;

    @GetMapping
    public List<YuGiOhSetDTO> getAll() {
        return yuGiOhSetService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<YuGiOhSetDTO> getById(@PathVariable Ulid id) {
        return yuGiOhSetService.findById(id);
    }

    @PutMapping
    public void saveSet(@RequestBody YuGiOhSetDTO set) {
        createSet(set);
    }

    @PostMapping
    public Ulid createSet(@RequestBody YuGiOhSetDTO set) {
        return yuGiOhSetService.save(set);
    }

}
