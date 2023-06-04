package nsu.philharmonia.controllers;


import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.SponsorDTO;
import nsu.philharmonia.model.entities.Sponsor;
import nsu.philharmonia.services.SponsorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping
    public ResponseEntity<SponsorDTO> addSponsor(@RequestBody SponsorDTO sponsor) {
        return sponsorService.saveSponsor(sponsor);
    }

    @PutMapping
    public ResponseEntity<SponsorDTO> updateSponsor(@RequestBody SponsorDTO sponsor) {
        return sponsorService.saveSponsor(sponsor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsor(@PathVariable("id") @Positive Long id) {
        return sponsorService.deleteSponsor(id);
    }
}
