import React, {useEffect, useState} from 'react';
import {getAllGenres} from "../services/GenreService";

const Genres = () => {

    const [genres, setGenres] = useState([]);

    useEffect(() => {
        getAllGenres().then(genres => {
            setGenres(genres);
            console.log(genres);
        })
    }, [])


    return (
        <div>
            <div>
                <h2>Жанры</h2>
                <table className={"table"}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        genres.map(genre => (
                            <tr key={genre.id}>
                                <td>{genre.id}</td>
                                <td>{genre.name}</td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Genres;
