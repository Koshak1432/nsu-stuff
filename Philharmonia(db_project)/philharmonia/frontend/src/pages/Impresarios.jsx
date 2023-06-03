import React, {useEffect, useState} from 'react';
import MyButton from "../components/UI/button/MyButton";
import {deleteArtist} from "../services/ArtistService";
import {getAllImpresarios, getArtistsByImpresarioId, getImpresariosByArtistId} from "../services/ImpresarioService";

const Impresarios = () => {

    const [impresarios, setImpresarios] = useState([]);
    const [artists, setArtists] = useState([]);


    const onRowClick = (impresarioInRow) => {
        getArtistsByImpresarioId(impresarioInRow.id).then(artists => {
            setArtists(artists);
            console.log(artists);
        });
        // setArtist(impresarioInRow);
    }

    useEffect(() => {
        getAllImpresarios().then(impresarios => {
            setImpresarios(impresarios);
            console.log(impresarios);
    })}, [])


    return (
        <div>
            <div>
                <h2>Импресарио</h2>
                <table className={"table"}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Фамилия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        impresarios.map(impresario => (
                            <tr key={impresario.id} onClick={() => onRowClick(impresario)}>
                                <td>{impresario.id}</td>
                                <td>{impresario.name}</td>
                                <td>{impresario.surname}</td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
            </div>

            <h2>Артисты</h2>
            <table className={"table"}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Фамилия</th>
                    <th>Жанры</th>
                </tr>
                </thead>
                <tbody>
                {
                    artists.map(artist => (
                        <tr key={artist.id}>
                            <td>{artist.id}</td>
                            <td>{artist.name}</td>
                            <td>{artist.surname}</td>
                            <td>{artist.genres.map(g => {
                                return <li key={g.name}>{g.name}</li>
                            })
                            }</td>
                        </tr>
                    ))
                }
                </tbody>
            </table>
        </div>
    );
};

export default Impresarios;
