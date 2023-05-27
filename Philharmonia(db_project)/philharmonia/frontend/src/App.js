import React, {useState} from 'react';
import ListArtistComponent from "./components/ListArtistComponent";
import MyButton from "./components/UI/button/MyButton";
import MyInput from "./components/UI/input/MyInput";
import ModalWindow from "./components/UI/modal/ModalWindow";

function App() {

    const [artist, setArtist] = useState({name: '', surname: ''});

    const addNew = (event) => {
        event.preventDefault(); //to disable button submit type in form
        console.log(artist);
        // validate and send to server?
        setArtist({name: '', surname: ''});
    }

    const [modalActive, setVisible] = useState(false);

    return (
        <div className="App">
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
                    value = {artist.surname}
                    onChange={e => setArtist({...artist, surname: e.target.value})}

                />
                <MyButton onClick={addNew} >Создать артиста</MyButton>
            </form>
            <MyButton onClick={() => setVisible(true)} >открыть модальное окно</MyButton>
            <ModalWindow visible={modalActive} setVisible={setVisible}> сюда надо форму запихать как отдельный компонент</ModalWindow>
        </div>
    );
}

export default App;
