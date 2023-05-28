import About from "../../pages/About";
import Artists from "../../pages/Artists";
import Impresarios from "../../pages/Impresarios";
import Buildings from "../../pages/Buildings";
import Performances from "../../pages/Performances";
import Contests from "../../pages/Contests";
import Login from "../../pages/Login";
import Error from "../../pages/Error";


export const privateRoutes = [
    {path: "/login", element: <Login/>, exact: true},
    {path: "/about", element: <About/>, exact: true},
    {path: "/artists", element: <Artists/>, exact: true},
    {path: "/impresarios", element: <Impresarios/>, exact: true},
    {path: "/buildings", element: <Buildings/>, exact: true},
    {path: "/performances", element: <Performances/>, exact: true},
    {path: "/contests", element: <Contests/>, exact: true},
    {path: "/error", element: <Error/>, exact: true},
    {path: "*", element: <Error/>, exact: true},
]

export const publicRoutes = [
    {path: "/login", element: <Login/>, exact: true},
    {path: "/about", element: <About/>, exact: true},
    // {path: "/artists", element: <Artists/>, exact: true},
    // {path: "/impresarios", element: <Impresarios/>, exact: true},
    // {path: "/buildings", element: <Buildings/>, exact: true},
    // {path: "/performances", element: <Performances/>, exact: true},
    // {path: "/contests", element: <Contests/>, exact: true},
    // {path: "/error", element: <Error/>, exact: true},
    {path: "*", element: <Login/>, exact: true},
]