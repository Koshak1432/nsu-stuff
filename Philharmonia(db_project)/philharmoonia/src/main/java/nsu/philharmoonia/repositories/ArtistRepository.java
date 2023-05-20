package nsu.philharmoonia.repositories;

import nsu.philharmoonia.model.entities.Artist;
import nsu.philharmoonia.model.entities.Impresario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, Long> {

    // todo also need to get genres, but this will be in another query?
    @Query(value = """
            SELECT * FROM artist
            WHERE id IN
            (
            	SELECT artist_id FROM artist_to_genre AS ag
            	GROUP BY
            		ag.artist_id
            	HAVING
            		COUNT(*) > 1
            )
            """, nativeQuery = true)
    Collection<Artist> findWithManyGenres();

    @Query(value = """
            SELECT * FROM artist
            WHERE id IN
            (
            	SELECT artist_id FROM artist_to_genre
            	WHERE genre_id = :genreId
            )
            """, nativeQuery = true)
    Collection<Artist> findByGenre(@Param("genreId") Long genreId);

    @Query(value = """
            SELECT * FROM artist
            WHERE id IN
            (
            	SELECT artist_id FROM artist_to_impresario
            	WHERE impresario_id = :impresarioId
            )
            """, nativeQuery = true)
    Collection<Artist> findByImpresario(@Param("impresarioId") Long impresarioId);


    // todo refactor to date since dont take part in performances
    @Query(value = """
            SELECT * FROM artist
            WHERE id NOT IN
            (
                SELECT artist_id FROM contest_place JOIN performance p ON p.id = contest_place.performance_id
                WHERE CURRENT_DATE - performance_date <= :days
            )
            """, nativeQuery = true)
    Collection<Artist> findNotInContestForTime(@Param("days") Long days);
}