import React, {useEffect, useState} from 'react';
import {getAllPerformances} from "../services/PerformanceService";

const Performances = () => {

    const [performances, setPerformances] = useState([]);

    useEffect(() => {
        getAllPerformances().then(performances => {
            setPerformances(performances);
            console.log(performances);
        })
    }, [])


    return (
        <div>
            <div>
                <h2>Выступления</h2>
                <table className={"table"}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Тип</th>
                        <th>Организатор</th>
                        <th>Сооружение</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        performances.map(performances => (
                            <tr key={performances.id}>
                                <td>{performances.id}</td>
                                <td>{performances.name}</td>
                                <td>{performances.typeName}</td>
                                <td>{performances.sponsor.name} {performances.sponsor.surname}</td>
                                <td>{performances.building.name} ({performances.building.typeName})</td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Performances;
