package nsu.philharmoonia.repositories;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Impresario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository

public interface ImpresarioRepository extends CrudRepository<Impresario, Long> {


    @Query(value = """
        SELECT * FROM impresario
        WHERE id IN
        (
            SELECT artist_id FROM artist_to_impresario
            WHERE artist_id = :artistId
        )
""", nativeQuery = true)
    Collection<Impresario> findByArtist(@Param("artistId") Long artistId);

//    @Query(value = """
//SELECT * from impresario
//            """)
//    Collection<Impresario> findByGenre(@Param("genreId") Long genreId);
}