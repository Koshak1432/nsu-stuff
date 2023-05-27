import React, {useState} from 'react';
import axios from "axios";
import MyButton from "../components/UI/button/MyButton";
import ModalWindow from "../components/UI/modal/ModalWindow";
import MyInput from "../components/UI/input/MyInput";


function Artists() {
    const [artist, setArtist] = useState({name: '', surname: ''});
    const [modalActive, setVisible] = useState(false);

    const addNew = (event) => {
        event.preventDefault(); //to disable button submit type in form
        console.log(artist);
        // validate and send to server?
        setArtist({name: '', surname: ''});
        setVisible(false);
    }

    async function fetchArtists() {
        const response = await axios.get('http://localhost:8080/api/v1/artists');
        //set artists
        console.log(response);
    }

    return (
        <div className="App">

            <MyButton onClick={() => setVisible(true)}>открыть модальное окно</MyButton>
            <ModalWindow visible={modalActive} setVisible={setVisible}>
                <form>
                    <MyInput
                        type={"text"}
                        placeholder={"Имя"}
                        onChange={e => setArtist({...artist, name: e.target.value})}
                        value={artist.name}
                    />
                    <MyInput
                        type={"text"}
                        placeholder={"Фамилия"}
                        value={artist.surname}
                        onChange={e => setArtist({...artist, surname: e.target.value})}

                    />
                    <MyButton onClick={addNew}>Создать артиста</MyButton>
                </form>
            </ModalWindow>
            <MyButton onClick={fetchArtists}>Get artists</MyButton>
        </div>

    );
}

export default Artists;
