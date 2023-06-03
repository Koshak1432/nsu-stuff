import React, {useEffect, useState} from 'react';
import {getAllBuildings} from "../../services/BuildingService";

const Buildings = () => {

    const [buildings, setBuildings] = useState([]);

    useEffect(() => {
        getAllBuildings().then(buildings => {
            setBuildings(buildings);
            console.log(buildings);
        })}, [])

    //todo тут же вставить кнопки посмотреть театры, эстрады и прочее и отфильтровать таблицу
    return (
        <div>
            <div>
                <h2>Сооружения</h2>
                <table className={"table"}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Тип</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        buildings.map(building => (
                            <tr key={building.id}>
                                <td>{building.id}</td>
                                <td>{building.name}</td>
                                <td>{building.typeName}</td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Buildings;
