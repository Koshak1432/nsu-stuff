package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.SponsorDTO;
import nsu.philharmonia.model.entities.Sponsor;
import nsu.philharmonia.repositories.SponsorRepository;
import nsu.philharmonia.services.SponsorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SponsorServiceImpl implements SponsorService {

    private final SponsorRepository sponsorRepository;
    private final ModelMapper mapper;

    @Autowired
    public SponsorServiceImpl(SponsorRepository sponsorRepository, ModelMapper mapper) {
        this.sponsorRepository = sponsorRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<List<SponsorDTO>> getAll() {
        List<Sponsor> sponsors = (List<Sponsor>) sponsorRepository.findAll();
        List<SponsorDTO> sponsorDTOS = sponsors.stream().map(sponsor -> mapper.map(sponsor, SponsorDTO.class)).toList();
        return new ResponseEntity<>(sponsorDTOS, HttpStatus.OK);
    }
}
