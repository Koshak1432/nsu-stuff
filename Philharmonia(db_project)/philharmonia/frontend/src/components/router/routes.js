import Artists from "../../pages/Artists";
import Impresarios from "../../pages/Impresarios";
import Buildings from "../../pages/buildings/Buildings";
import Performances from "../../pages/Performances";
import Contests from "../../pages/Contests";
import Login from "../../pages/Login";
import Error from "../../pages/Error";
import Theaters from "../../pages/buildings/Theaters";
import Estrades from "../../pages/buildings/Estrades";
import Palaces from "../../pages/buildings/Palaces";
import Venues from "../../pages/buildings/Venues";
import Sponsors from "../../pages/Sponsors";
import Genres from "../../pages/Genres";


export const privateRoutes = [
    {path: "/login", element: <Login/>, exact: true},
    {path: "/artists", element: <Artists/>, exact: true},
    {path: "/impresarios", element: <Impresarios/>, exact: true},
    {path: "/buildings", element: <Buildings/>, exact: true},
    {path: "/performances", element: <Performances/>, exact: true},
    {path: "/contests", element: <Contests/>, exact: true},
    {path: "/error", element: <Error/>, exact: true},
    {path: "*", element: <Error/>, exact: true},
    {path: "/theaters", element: <Theaters/>, exact: true},
    {path: "/estrades", element: <Estrades/>, exact: true},
    {path: "/palaces", element: <Palaces/>, exact: true},
    {path: "/venues", element: <Venues/>, exact: true},
    {path: "/sponsors", element: <Sponsors/>, exact: true},
    {path: "/genres", element: <Genres/>, exact: true},
]

export const publicRoutes = [
    {path: "/login", element: <Login/>, exact: true},
    // {path: "/artists", element: <Artists/>, exact: true},
    // {path: "/impresarios", element: <Impresarios/>, exact: true},
    // {path: "/buildings", element: <Buildings/>, exact: true},
    // {path: "/performances", element: <Performances/>, exact: true},
    // {path: "/contests", element: <Contests/>, exact: true},
    // {path: "/error", element: <Error/>, exact: true},
    {path: "*", element: <Login/>, exact: true},
]