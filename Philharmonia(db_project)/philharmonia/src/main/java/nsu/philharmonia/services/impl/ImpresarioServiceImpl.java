package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.dto.ImpresarioDTO;
import nsu.philharmonia.model.entities.Artist;
import nsu.philharmonia.model.entities.Impresario;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.ImpresarioRepository;
import nsu.philharmonia.services.ImpresarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ImpresarioServiceImpl implements ImpresarioService {

    private final ModelMapper mapper;
    private final ImpresarioRepository impresarioRepository;

    @Autowired
    public ImpresarioServiceImpl(ImpresarioRepository impresarioRepository, ModelMapper modelMapper) {
        this.impresarioRepository = impresarioRepository;
        this.mapper = modelMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<ImpresarioDTO> addImpresario(ImpresarioDTO impresario) {
        Impresario saved = impresarioRepository.findById(impresario.getId()).map(i -> {
            i.setName(impresario.getName());
            i.setSurname(impresario.getSurname());
            return impresarioRepository.save(i);
        }).orElseGet(() -> impresarioRepository.save(mapper.map(impresario, Impresario.class)));
        return new ResponseEntity<>(mapper.map(saved, ImpresarioDTO.class), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteImpresario(Long id) {
        impresarioRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ImpresarioDTO>> getAll() {
        List<Impresario> impresarios = (List<Impresario>) impresarioRepository.findAll();
        List<ImpresarioDTO> impresarioDTOS = impresarios.stream()
                .map(impresario -> mapper.map(impresario, ImpresarioDTO.class))
                .toList();
        return new ResponseEntity<>(impresarioDTOS, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<ImpresarioDTO>> getByArtistId(Long id) {
        List<Impresario> impresarios = (List<Impresario>) impresarioRepository.findByArtist(id);
        List<ImpresarioDTO> impresarioDTOS = impresarios.stream()
                .map(impresario -> mapper.map(impresario, ImpresarioDTO.class))
                .toList();
        return new ResponseEntity<>(impresarioDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ArtistDTO>> getArtistsByImpresarioId(Long id) throws NotFoundException {
        Impresario impresario = findImpresario(id);
        Set<Artist> artists = impresario.getArtists();
        List<ArtistDTO> artistDTOS = artists.stream().map(artist -> mapper.map(artist, ArtistDTO.class)).toList();
        return new ResponseEntity<>(artistDTOS, HttpStatus.OK);
    }



    private Impresario findImpresario(Long id) throws NotFoundException {
        Optional<Impresario> impresario = impresarioRepository.findById(id);
        if (impresario.isEmpty()) {
            throw new NotFoundException("Not found impresario");
        }
        return impresario.get();
    }
}
