package com.hws.travel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hws.travel.entity.enums.Mobilite;
import com.hws.travel.entity.enums.Saison;
import com.hws.travel.entity.enums.PourQui;

@RestController
@RequestMapping("/api/enums")
public class EnumController {
    @GetMapping("/mobilite")
    public Mobilite[] getMobilites() {
        return Mobilite.values();
    }
    @GetMapping("/saison")
    public Saison[] getSaisons() {
        return Saison.values();
    }
    @GetMapping("/pourqui")
    public PourQui[] getPourQui() {
        return PourQui.values();
    }
}
