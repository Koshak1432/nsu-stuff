import React from 'react';
import {Navigate, Route, Routes} from "react-router-dom";
import About from "../pages/about";
import Artists from "../pages/Artists";
import Error from "../pages/Error";

const AppRouter = () => {
    return (
        <Routes>
            <Route path={"/about"} element={<About/>}/>
            <Route path={"/artists"} element={<Artists/>}/>
            <Route path={"/error"} element={<Error/>}/>
            <Route path={"/*"} element={<Navigate to={"/error"} replace/>}/>
        </Routes>
    );
};

export default AppRouter;
