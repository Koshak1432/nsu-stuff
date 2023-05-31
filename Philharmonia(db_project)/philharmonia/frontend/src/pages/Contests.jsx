import React, {useState} from 'react';
import MyButton from "../components/UI/button/MyButton";
import {deleteArtist, getArtistById} from "../services/ArtistService";
import {getImpresariosByArtistId} from "../services/ImpresarioService";
import {getContestById} from "../services/ContestService";

const Contests = () => {
    const [contests, setContests] = useState([]);
    const [contest, setContest] = useState({});
    const [modalAdd, setModalAdd] = useState(false);
    const [artists, setArtists] = useState([]);

    const onRowClick = (contestInRow) => {
        // getArtistById(contestInRow.id).then(contest => setcontest));
        setContest(contestInRow);
    }
    
    return (
        <div>
            <h1>Контесты и распределение людей по местам</h1>
            <table className={"table"}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Название</th>
                    <th>Дата проведения</th>
                </tr>
                </thead>
                <tbody>
                {contests.map(contest => (
                    <tr key={contest.id} onClick={() => onRowClick(contest)}>
                        <td>{contest.id}</td>
                        <td>{contest.name}</td>
                        <td>{contest.date}</td>

                        {/*<td>*/}
                        {/*    <MyButton onClick={() => handleUpdateArtist(contest)}>Обновить</MyButton>*/}
                        {/*    <MyButton onClick={() => deleteArtist(contest.id).then(refresh)}>Удалить</MyButton>*/}
                        {/*</td>*/}
                    </tr>
                ))
                }
                </tbody>
            </table>
        </div>
    );
};

export default Contests;
