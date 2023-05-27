import React from 'react';
import {Link} from "react-router-dom";

const Navbar = () => {
    return (
        <div className={"navbar"}>
            <div className={"navbar_links"}>
                <Link to={"/about"}>О сайте</Link>
                <Link to={"/artists"}>Артисты</Link>
            </div>
        </div>
    );
};

export default Navbar;
