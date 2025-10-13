package io.proj3ct.SpringDemoBot.Cash.VapecomponyRepository_working_withBD;

import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.service.VapecomponyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vapecompony")
@RequiredArgsConstructor
public class VapecomponyController {

    private final VapecomponyService vapecomponyService;

    @PostMapping("/add")
    public ResponseEntity<Vapecompony> addVapecompony(@RequestBody VapecomponyDTO dto) {

        Vapecompony saved = vapecomponyService.save(dto);
        return ResponseEntity.ok(saved);
    }
}
