package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Impresario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, Long> {

    @Query(value = """
            SELECT * FROM artist
            WHERE id IN
            (
            	SELECT artist_id FROM artist_to_genre
            	WHERE genre_id = :genreId
            )
            """, nativeQuery = true)
    Collection<Artist> findByGenre(@Param("genreID") Long genreId);

    @Query(value = """
            SELECT * FROM artist
            WHERE id IN
            (
            	SELECT artist_id FROM artist_to_impresario
            	WHERE impresario_id = :impresarioId
            )
            """, nativeQuery = true)
    Collection<Artist> findByImpresario(@Param("impresarioId") Long impresarioId);
}