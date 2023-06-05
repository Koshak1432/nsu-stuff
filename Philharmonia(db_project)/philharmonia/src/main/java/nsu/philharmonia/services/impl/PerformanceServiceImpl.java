package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.*;
import nsu.philharmonia.model.entities.*;
import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.*;
import nsu.philharmonia.repositories.buildings.BuildingRepository;
import nsu.philharmonia.services.PerformanceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final ContestPlaceRepository contestDistributionRepository;
    private final PerformanceTypeRepository performanceTypeRepository;
    private final SponsorRepository sponsorRepository;
    private final BuildingRepository buildingRepository;
    private final ArtistRepository artistRepository;
    private final ModelMapper mapper;

    @Override
    public ResponseEntity<List<PerformanceDTO>> getAllContests() {
        List<Performance> contests = performanceRepository.findPerformancesByPerformanceTypeName("конкурс");
        List<PerformanceDTO> contestDTOS = contests.stream().map(
                contest -> mapper.map(contest, PerformanceDTO.class)).toList();
        return new ResponseEntity<>(contestDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deletePerformanceDistribution(PerformanceDistributionDTO distribution) throws
            InvalidInputException {
        Performance performance = performanceRepository.findByName(distribution.getPerformance().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid performance name"));
        Artist artist = artistRepository.findByName(distribution.getArtist().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid artist name"));

        Set<Artist> artists = performance.getArtists();
        Set<Performance> performances = artist.getPerformances();
        artists.remove(artist);
        performances.remove(performance);
        artist.setPerformances(performances);
        performance.setArtists(artists);
        artistRepository.save(artist);
        performanceRepository.save(performance);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> savePerformanceDistribution(PerformanceDistributionDTO distribution) throws
            InvalidInputException {
        Performance performance = performanceRepository.findByName(distribution.getPerformance().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid performance name"));
        Artist artist = artistRepository.findByName(distribution.getArtist().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid artist name"));

        Set<Performance> performances = artist.getPerformances();
        if (! performances.contains(performance)) {
            performances.add(performance);
            artist.setPerformances(performances);
        }
        Set<Artist> artists = performance.getArtists();
        if (! artists.contains(artist)) {
            artists.add(artist);
            performance.setArtists(artists);
        }
        artistRepository.save(artist);
        performanceRepository.save(performance);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PerformanceDistributionDTO>> getPerformanceDistribution() {
        List<Artist> artists = (List<Artist>) artistRepository.findAll();
        List<PerformanceDistributionDTO> distribution = new ArrayList<>();
        for (Artist artist : artists) {
            for (Performance performance : artist.getPerformances()) {
                PerformanceDistributionDTO perf = new PerformanceDistributionDTO();
                perf.setArtist(mapper.map(artist, ArtistDTO.class));
                perf.setPerformance(mapper.map(performance, PerformanceDTO.class));
                IdKeyDTO key = new IdKeyDTO();
                key.setPerformanceId(performance.getId());
                key.setArtistId(artist.getId());
                perf.setId(key);
                distribution.add(perf);
            }
        }
        return new ResponseEntity<>(distribution, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteContestDistribution(IdKeyDTO key) {
        ContestPlaceKey k = mapper.map(key, ContestPlaceKey.class);
        System.out.println("KEY1: performance: " + key.getPerformanceId() + ", artist: " + key.getArtistId());
        System.out.println("KEY:" + k);
        contestDistributionRepository.deleteById(k);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<ContestPlaceDTO> saveContestDistribution(ContestPlaceDTO contestPlace) throws
            InvalidInputException {
        Performance performance = performanceRepository.findByName(contestPlace.getPerformance().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid performance name"));
        Artist artist = artistRepository.findByName(contestPlace.getArtist().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid artist name"));

        ContestPlace place = contestDistributionRepository.findContestPlaceByArtistAndPerformance(artist,
                                                                                                  performance).orElseGet(
                ContestPlace::new);
        ContestPlaceKey key = new ContestPlaceKey(performance.getId(), artist.getId());
        place.setContestPlaceKey(key);
        place.setArtist(artist);
        place.setPlace(contestPlace.getPlace());
        place.setPerformance(performance);
        ContestPlace saved = contestDistributionRepository.save(place);
        return new ResponseEntity<>(mapper.map(saved, ContestPlaceDTO.class), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<PerformanceDTO> savePerformance(PerformanceDTO performance) throws InvalidInputException {
        PerformanceType performanceType = performanceTypeRepository.findByName(performance.getTypeName()).orElseThrow(
                () -> new InvalidInputException("Invalid performance type"));
        Sponsor sponsor = sponsorRepository.findSponsorByNameAndSurname(performance.getSponsor().getName(),
                                                                        performance.getSponsor().getSurname()).orElseThrow(
                () -> new InvalidInputException("Invalid sponsor"));
        Building building = buildingRepository.findBuildingByName(performance.getBuilding().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid building"));

        Performance saved = performanceRepository.findById(performance.getId()).map(p -> {
            p.setName(performance.getName());
            p.setSponsor(sponsor);
            p.setPerformanceType(performanceType);
            p.setBuilding(building);
            p.setPerformanceDate(performance.getPerformanceDate());
            return performanceRepository.save(p);
        }).orElseGet(() -> {
            Performance newPerformance = mapper.map(performance, Performance.class);
            newPerformance.setPerformanceType(performanceType);
            newPerformance.setSponsor(sponsor);
            newPerformance.setBuilding(building);
            return performanceRepository.save(newPerformance);
        });
        return new ResponseEntity<>(mapper.map(saved, PerformanceDTO.class), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deletePerformance(Long id) {
        performanceRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ContestPlaceDTO>> getContestDistribution() {
        List<ContestPlace> contestPlaces = (List<ContestPlace>) contestDistributionRepository.findAll();
        List<ContestPlaceDTO> contestPlaceDTOS = contestPlaces.stream().map(
                contest -> mapper.map(contest, ContestPlaceDTO.class)).toList();
        return new ResponseEntity<>(contestPlaceDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ContestPlaceDTO>> getDistributionByContestId(Long id) {
        List<ContestPlace> contestPlaces = contestDistributionRepository.findContestPlaceByPerformanceId(id);
        List<ContestPlaceDTO> contestPlaceDTOS = contestPlaces.stream().map(
                contest -> mapper.map(contest, ContestPlaceDTO.class)).toList();
        return new ResponseEntity<>(contestPlaceDTOS, HttpStatus.OK);
    }

    @Autowired
    public PerformanceServiceImpl(PerformanceRepository performanceRepository, ModelMapper mapper,
                                  ContestPlaceRepository contestDistributionRepository,
                                  PerformanceTypeRepository performanceTypeRepository,
                                  SponsorRepository sponsorRepository, BuildingRepository buildingRepository,
                                  ArtistRepository artistRepository) {
        this.performanceRepository = performanceRepository;
        this.mapper = mapper;
        this.contestDistributionRepository = contestDistributionRepository;
        this.performanceTypeRepository = performanceTypeRepository;
        this.sponsorRepository = sponsorRepository;
        this.buildingRepository = buildingRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public ResponseEntity<List<PerformanceDTO>> getAll() {
        List<Performance> performances = (List<Performance>) performanceRepository.findAll();
        List<PerformanceDTO> performanceDTOS = performances.stream().map(
                p -> mapper.map(p, PerformanceDTO.class)).toList();
        return new ResponseEntity<>(performanceDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PerformanceDTO> getById(Long id) throws NotFoundException {
        Performance performance = findPerformance(id);
        PerformanceDTO performanceDTO = mapper.map(performance, PerformanceDTO.class);
        return new ResponseEntity<>(performanceDTO, HttpStatus.OK);
    }

    private Performance findPerformance(Long id) throws NotFoundException {
        Optional<Performance> performanceOptional = performanceRepository.findById(id);
        if (performanceOptional.isEmpty()) {
            throw new NotFoundException("Couldn't find performance");
        }
        return performanceOptional.get();
    }
}
