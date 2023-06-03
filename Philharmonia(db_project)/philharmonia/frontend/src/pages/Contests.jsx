import React, {useEffect, useState} from 'react';
import {getAllContests} from "../services/ContestService";

const Contests = () => {

    const [contests, setContests] = useState([]);

    useEffect(() => {
        getAllContests().then(contests => {
            setContests(contests);
            console.log(contests);
        })
    }, [])

    //todo как название выцепить из конкурса?
    //todo как-то надо артистов и выступление соотнести, подтянуть по составному ключу артистов и выступления
    //todo мб вообще убрать таблицы тип выступления и контест держать как поле в первормансе? так себе решение
    return (
        <div>
            <h1>Конкурсы</h1>
            <table className={"table"}>
                <thead>
                <tr>
                    <th>Артист</th>
                    <th>Выступление</th>
                    <th>Название</th>
                    <th>Место</th>
                </tr>
                </thead>
                <tbody>
                {contests.map(contest => (
                    <tr key={contest.id}>
                        <td>{contest.id.artistId}</td>
                        <td>{contest.id.performanceId}</td>
                        <td>{contest.name}</td>
                    </tr>
                ))
                }
                </tbody>
            </table>
        </div>
    );
};

export default Contests;
