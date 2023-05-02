package nsu.philharmoonia.model.repositories;

import nsu.philharmoonia.model.entities.Genre;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository

public interface GenreRepository extends CrudRepository<Genre, Long> {
    @Query(value = """
            SELECT * FROM genre
            WHERE id IN
            (
                SELECT genre_id FROM artist_to_genre
                WHERE artist_id = :artistId
            )
            """, nativeQuery = true)
    Collection<Genre> findByArtistId(@Param("artistId") Long artistId);
}