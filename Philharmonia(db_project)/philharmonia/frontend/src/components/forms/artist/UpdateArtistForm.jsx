import React, {useEffect, useState} from 'react';
import MyInput from "../../UI/input/MyInput";
import MyButton from "../../UI/button/MyButton";

const clearArtist = {
    id: 0,
    name: '',
    surname: '',
    genres: []
}
const UpdateArtistForm = ({setClose, update, initialArtist}) => {
    const [artist, setArtist] = useState(clearArtist);

    useEffect(() => {
        if (initialArtist) {
            setArtist(initialArtist);
            console.log(initialArtist);
        }
    }, [initialArtist]);


    const updateArtist = (event) => {
        event.preventDefault(); //to disable button submit type in form
        console.log(artist);
        update(artist); // function that will add artist to the server
        // validate and send to server?
        setClose();
        setArtist(clearArtist);
    }

    return (
        <form>
            <h2>Добавление/обновление артиста</h2>
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
            <MyButton onClick={updateArtist}>Обновить</MyButton>
        </form>
    );
};

export default UpdateArtistForm;
