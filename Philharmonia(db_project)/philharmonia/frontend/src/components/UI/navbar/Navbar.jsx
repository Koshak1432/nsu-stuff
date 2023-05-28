import React, {useContext} from 'react';
import {Link} from "react-router-dom";
import cl from './Navbar.module.css';
import {AuthContext} from "../../context/AuthContext";
import MyButton from "../button/MyButton";

const Navbar = () => {
    const {isAuth, setIsAuth} = useContext(AuthContext);
    const logout = () => {
        setIsAuth(false);
        localStorage.removeItem("auth");
    }

    return (
        <div className={cl.navbar}>
            <div>
                <Link className={cl.link} to={"/login"}>Войти</Link>
                <Link className={cl.link} to={"/logout"} onClick={logout}>Выйти</Link>
                <Link className={cl.link} to={"/about"}>О сайте</Link>
                <Link className={cl.link} to={"/artists"}>Артисты</Link>
                <Link className={cl.link} to={"/impresarios"}>Импресарио</Link>
                <Link className={cl.link} to={"/buildings"}>Сооружения</Link>
                <Link className={cl.link} to={"/performances"}>Выступления</Link>
                <Link className={cl.link} to={"/contests"}>Конкурсы</Link>
            </div>
        </div>
    );
};

export default Navbar;
