import React, {useEffect, useState} from 'react';
import MyButton from "../components/UI/button/MyButton";
import ModalWindow from "../components/UI/modal/ModalWindow";
import MyInput from "../components/UI/input/MyInput";
import {getAll} from "../services/ArtistService";

// modal window with form for add, another for update
function Artists() {
    const [artists, setArtists] = useState([])
    const [modalActive, setVisible] = useState(false);

    const addNew = (event) => {
        event.preventDefault(); //to disable button submit type in form
        // console.log(artist);
        // validate and send to server?
        // setArtists();
        setVisible(false);
    }

    useEffect( () => {
        let mounted = true;
        // getAll().then(artists => artists?.map(artist => {
        //     setArtist(artist);
        //     // console.log(artist);
        // }));
        getAll().then(artists => {
            if (mounted) {
                setArtists(artists);
            }
        });
        return () => mounted = false;
    }, [artists]);


    return (
        <div className="App">
            <MyButton onClick={() => setVisible(true)}>Добавить артиста(открыть окно)</MyButton>
            <ModalWindow visible={modalActive} setVisible={setVisible}>
                <form>
                    <MyInput
                        type={"text"}
                        placeholder={"Имя"}
                        // onChange={e => setArtists({...artists, name: e.target.value})}
                        value={artists.name}
                    />
                    <MyInput
                        type={"text"}
                        placeholder={"Фамилия"}
                        value={artists.surname}
                        // onChange={e => setArtist({...artist, surname: e.target.value})}

                    />
                    <MyButton onClick={addNew}>Создать артиста</MyButton>
                </form>
            </ModalWindow>
            {/*<MyButton onClick={fetchArtists}>Get artists</MyButton>*/}

            <div>
                <h2>Ну артисты</h2>
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
                        {artists.map(artist => (
                            <tr key={artist.id}>
                                <td>{artist.id}</td>
                                <td>{artist.name}</td>
                                <td>{artist.surname}</td>
                                {/*<td>{artist.genres.map(genre => genre.name)}</td>*/}
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
