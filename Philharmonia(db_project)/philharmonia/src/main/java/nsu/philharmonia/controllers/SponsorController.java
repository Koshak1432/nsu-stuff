package nsu.philharmonia.controllers;


import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.SponsorDTO;
import nsu.philharmonia.model.entities.Sponsor;
import nsu.philharmonia.services.SponsorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH + "/sponsors")
@Validated
public class SponsorController {

    private final SponsorService sponsorService;

    @Autowired
    public SponsorController(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }


    @GetMapping
    public ResponseEntity<List<SponsorDTO>> getAll() {
        return sponsorService.getAll();
    }
}
