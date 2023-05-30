package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.ImpresarioDTO;
import nsu.philharmonia.model.entities.Impresario;
import nsu.philharmonia.repositories.ImpresarioRepository;
import nsu.philharmonia.services.ImpresarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ResponseEntity<List<ImpresarioDTO>> getAll() {
        List<Impresario> impresarios = (List<Impresario>) impresarioRepository.findAll();
        List<ImpresarioDTO> impresarioDTOS = impresarios.stream()
                .map(impresario -> mapper.map(impresario, ImpresarioDTO.class))
                .toList();
        return new ResponseEntity<>(impresarioDTOS, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<ImpresarioDTO>> findByArtistId(Long id) {
        List<Impresario> impresarios = (List<Impresario>) impresarioRepository.findByArtist(id);
        List<ImpresarioDTO> impresarioDTOS = impresarios.stream()
                .map(impresario -> mapper.map(impresario, ImpresarioDTO.class))
                .toList();
        return new ResponseEntity<>(impresarioDTOS, HttpStatus.OK);
    }
}
