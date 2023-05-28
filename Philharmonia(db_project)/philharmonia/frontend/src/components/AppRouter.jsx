import React, {useContext} from 'react';
import {Route, Routes} from "react-router-dom";
import {privateRoutes, publicRoutes} from "./router/routes";
import {AuthContext} from "./context/AuthContext";

const AppRouter = () => {
    const {isAuth, isLoading} = useContext(AuthContext);

    if (isLoading) {
        return (
            <h2>Загрузка</h2>
        )
    }

    return (
        isAuth
        ?
            <Routes>
                {privateRoutes.map(route =>
                    <Route path={route.path} element={route.element} exact={route.exact} key={route.path}></Route>)}
            </Routes>
        :
            <Routes>
                {publicRoutes.map(route =>
                    <Route path={route.path} element={route.element} exact={route.exact} key={route.path}></Route>)}
            </Routes>

    );
};

export default AppRouter;
