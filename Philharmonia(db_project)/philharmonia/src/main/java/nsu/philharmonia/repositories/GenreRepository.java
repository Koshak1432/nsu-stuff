package nsu.philharmonia.repositories;

import nsu.philharmonia.model.entities.Genre;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    List<Genre> findByArtistId(@Param("artistId") Long artistId);


    Optional<Genre> findByName(String name);
}