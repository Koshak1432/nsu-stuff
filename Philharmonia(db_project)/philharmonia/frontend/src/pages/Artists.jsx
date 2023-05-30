import React, {useEffect, useState} from 'react';
import MyButton from "../components/UI/button/MyButton";
import ModalWindow from "../components/UI/modal/ModalWindow";
import {deleteArtist, getAll, postArtist, updateArtist} from "../services/ArtistService";
import AddArtistForm from "../components/forms/AddArtistForm";
import {getImpresariosByArtistId} from "../services/ImpresarioService";


function Artists() {
    const clearModals = {
        addModal: false,
        multigenreModal: false,
        genreModal: false,
        impresarioModal: false,
    }

    const [artists, setArtists] = useState([]);
    const [impresarios, setImpresarios] = useState([]);
    const [modals, setModals] = useState(clearModals);

    const modalsClose = {
        addModal: function () {
            return () => setModals({...modals, addModal: false});
        },
        multigenreModal: function () {
            return () => setModals({...modals, multigenreModal: false});
        },
        genreModal: function () {
            return () => setModals({...modals, genreModal: false});
        },
        impresarioModal: function () {
            return () => setModals({...modals, impresarioModal: false});
        },
    }

    useEffect( () => {
        let mounted = true;
        getAll().then(artists => {
            if (mounted) {
                setArtists(artists);
            }
        });

        return () => {
            mounted = false;
        }
    }, [artists]);

    const createArtist = (newArtist) => {
        postArtist(newArtist).then(response => console.log(response));
    }

    const onRowClick = (id) => {
        getImpresariosByArtistId(id).then(impresarios => setImpresarios(impresarios));
    }

    const handleUpdateArtist = (updArtist) => {
        updateArtist(updArtist);
    }

    const handleDeleteArtist = (id) => {
        deleteArtist(id);
    }


    return (
        <div className="App">
            <MyButton onClick={() => setModals({...modals, addModal: true})}>Добавить артиста</MyButton>
            <MyButton onClick={() => setModals({...modals, multigenreModal: true})}>Найти многожанровых артистов</MyButton>
            <MyButton onClick={() => setModals({...modals, genreModal: true})}>Найти артистов по жанру</MyButton>
            <MyButton onClick={() => setModals({...modals, impresarioModal: true})}>Найти артистов по импресарио</MyButton>
            <ModalWindow visible={modals.addModal} setClose={modalsClose.addModal()}>
                <h1>Добавить артиста</h1>
                <AddArtistForm create={createArtist} setClose={modalsClose.addModal()}/>
            </ModalWindow>
            <ModalWindow visible={modals.genreModal} setClose={modalsClose.genreModal()}>
                <AddArtistForm create={createArtist} setClose={modalsClose.genreModal()}/>
            </ModalWindow>
            <ModalWindow visible={modals.impresarioModal} setClose={modalsClose.impresarioModal()}>
                <AddArtistForm create={createArtist} setClose={modalsClose.impresarioModal()}/>
            </ModalWindow>

            <div>
                <h2>Артисты</h2>
                <table className={"table"}>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Имя</th>
                            <th>Фамилия</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        {artists.map(artist => (
                            <tr key={artist.id} onClick={() => onRowClick(artist.id)}>
                                <td>{artist.id}</td>
                                <td>{artist.name}</td>
                                <td>{artist.surname}</td>
                                <td>
                                    <MyButton onClick={() => updateArtist(artist.id)}>Обновить</MyButton>
                                    <MyButton onClick={() => deleteArtist(artist.id).then(response => console.log(response))}>Удалить</MyButton>
                                </td>
                            </tr>
                        ))
                        }
                    </tbody>
                </table>
            </div>

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
                    {impresarios.map(impresario => (
                        <tr key={impresario.id}>
                            <td>{impresario.id}</td>
                            <td>{impresario.name}</td>
                            <td>{impresario.surname}</td>
                        </tr>
                    ))
                    }
                    </tbody>
                </table>
            </div>

        </div>

    );
}

export default Artists;
