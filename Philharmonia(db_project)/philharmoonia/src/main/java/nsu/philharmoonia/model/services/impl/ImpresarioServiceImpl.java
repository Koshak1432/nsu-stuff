package nsu.philharmoonia.model.services.impl;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Impresario;
import nsu.philharmoonia.model.repositories.ImpresarioRepository;
import nsu.philharmoonia.model.services.ImpresarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ImpresarioServiceImpl implements ImpresarioService {
    private ImpresarioRepository impresarioRepository;

    @Autowired
    public ImpresarioServiceImpl(ImpresarioRepository impresarioRepository) {
        this.impresarioRepository = impresarioRepository;
    }

    @Override
    public Collection<Impresario> getArtistImpresarios(Artist artist) {
//        Collection<Impresario> impresarios = impresarioRepository.;
        return null;
    }
}
