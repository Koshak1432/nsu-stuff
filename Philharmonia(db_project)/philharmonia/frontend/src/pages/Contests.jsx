import React, {useEffect, useState} from 'react';
import {getAllContests, getContestDistributionById, getContestsDistribution} from "../services/PerformanceService";

const Contests = () => {

    const [contests, setContests] = useState([]);
    const [distribution, setDistribution] = useState([]);

    useEffect(() => {
        // getContestsDistribution().then(distribution => {
        //     setDistribution(distribution);
        //     console.log(distribution);
        // })

        getAllContests().then(contests => {
            setContests(contests);
            console.log(contests);
        })
    }, [])


    const onRowClick = (contest) => {
        getContestDistributionById(contest.id).then(distribution => {
            setDistribution(distribution);
            console.log(distribution);
        })


    }

    //todo как-то надо артистов и выступление соотнести, подтянуть по составному ключу артистов и выступления
    //todo мб вообще убрать таблицы тип выступления и контест держать как поле в перформансе? так себе решение
    return (
        <div>
            <div>
                <h1>Конкурсы</h1>
                <table className={"table"}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Организатор</th>
                        <th>Сооружение</th>
                    </tr>
                    </thead>
                    <tbody>
                    {contests.map(contest => (
                        <tr key={contest.id} onClick={() => onRowClick(contest)}>
                            <td>{contest.id}</td>
                            <td>{contest.name}</td>
                            <td>{contest.sponsor.name} {contest.sponsor.surname}</td>
                            <td>{contest.building.name}</td>
                        </tr>
                    ))
                    }
                    </tbody>
                </table>
            </div>

            {distribution.length > 0 &&
                <div>
                    <h2>Импресарио</h2>
                    <table className={"table"}>
                        <thead>
                        <tr>
                            <th>ID артиста</th>
                            <th>Место</th>
                        </tr>
                        </thead>
                        <tbody>
                        {distribution.map(distribution => (
                            <tr key={distribution.id.artistId}>
                                <td>{distribution.id.artistId}</td>
                                <td>{distribution.place}</td>
                            </tr>
                        ))
                        }
                        </tbody>
                    </table>
                </div>
            }
        </div>
    );
};

export default Contests;
