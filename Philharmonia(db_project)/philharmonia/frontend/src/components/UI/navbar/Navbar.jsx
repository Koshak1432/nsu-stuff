import React, {useContext} from 'react';
import {Link} from "react-router-dom";
import cl from './Navbar.module.css';
import {AuthContext} from "../../context/AuthContext";

const Navbar = () => {
    const {setIsAuth} = useContext(AuthContext);
    const logout = () => {
        setIsAuth(false);
        localStorage.removeItem("auth");
    }

    return (
        <div className={cl.navbar}>
            <div>
                <Link className={cl.link} to={"/login"}>Войти</Link>
                <Link className={cl.link} to={"/logout"} onClick={logout}>Выйти</Link>
                <Link className={cl.link} to={"/genres"}>Жанры</Link>
                <Link className={cl.link} to={"/artists"}>Артисты</Link>
                <Link className={cl.link} to={"/artists/distribution"}>Артисты-жанры</Link>
                <Link className={cl.link} to={"/impresarios"}>Импресарио</Link>
                <Link className={cl.link} to={"/impresarios/distribution"}>Артисты-импресарио</Link>
                <Link className={cl.link} to={"/performances"}>Выступления</Link>
                <Link className={cl.link} to={"/performances/distribution"}>Распределение выступлений</Link>
                <Link className={cl.link} to={"/contests/distribution"}>Распределение конкурсов</Link>
                <Link className={cl.link} to={"/sponsors"}>Спонсоры</Link>
                <Link className={cl.link} to={"/buildings"}>Сооружения</Link>
                <Link className={cl.link} to={"/buildings/types"}>Виды сооружений</Link>
                <Link className={cl.link} to={"/theaters"}>Театры</Link>
                <Link className={cl.link} to={"/palaces"}>Дворцы культуры</Link>
            </div>
        </div>
    );
};

export default Navbar;
