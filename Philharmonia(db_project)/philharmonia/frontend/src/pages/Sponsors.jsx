import React, {useEffect, useState} from 'react';
import {getAllImpresarios} from "../services/ImpresarioService";
import {getAllSponsors} from "../services/Sponsors";

const Sponsors = () => {

    const [sponsors, setSponsors] = useState([]);


    useEffect(() => {
        getAllSponsors().then(sponsors => {
            setSponsors(sponsors);
            console.log(sponsors);
        })}, [])

    return (
        <div>
            <div>
                <h2>Организаторы(спонсоры)</h2>
                <table className={"table"}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Фамилия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        sponsors.map(sponsor => (
                            <tr key={sponsor.id}>
                                <td>{sponsor.id}</td>
                                <td>{sponsor.name}</td>
                                <td>{sponsor.surname}</td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
            </div>
        </div>
    )
};

export default Sponsors;
