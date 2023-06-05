import Artists from "../../pages/Artists";
import Impresarios from "../../pages/Impresarios";
import Buildings from "../../pages/buildings/Buildings";
import Performances from "../../pages/Performances";
import ContestsDistribution from "../../pages/ContestsDistribution";
import Login from "../../pages/Login";
import Error from "../../pages/Error";
import Theaters from "../../pages/buildings/Theaters";
import Palaces from "../../pages/buildings/Palaces";
import Sponsors from "../../pages/Sponsors";
import Genres from "../../pages/Genres";
import PerformancesDistribution from "../../pages/PerformancesDistribution";
import ArtistToGenres from "../../pages/ArtistToGenres";
import ArtistToImpresario from "../../pages/ArtistToImpresario";


export const privateRoutes = [
    {path: "/login", element: <Login/>, exact: true},
    {path: "/artists", element: <Artists/>, exact: true},
    {path: "/impresarios", element: <Impresarios/>, exact: true},
    {path: "/buildings", element: <Buildings/>, exact: true},
    {path: "/performances", element: <Performances/>, exact: true},
    {path: "/contests/distribution", element: <ContestsDistribution/>, exact: true},
    {path: "/error", element: <Error/>, exact: true},
    {path: "*", element: <Error/>, exact: true},
    {path: "/theaters", element: <Theaters/>, exact: true},
    {path: "/palaces", element: <Palaces/>, exact: true},
    {path: "/sponsors", element: <Sponsors/>, exact: true},
    {path: "/genres", element: <Genres/>, exact: true},
    {path: "/performances/distribution", element: <PerformancesDistribution/>, exact: true},
    {path: "/artists/distribution", element: <ArtistToGenres/>, exact: true},
    {path: "/impresarios/distribution", element: <ArtistToImpresario/>, exact: true},
]

export const publicRoutes = [
    {path: "/login", element: <Login/>, exact: true},
    // {path: "/artists", element: <Artists/>, exact: true},
    // {path: "/impresarios", element: <Impresarios/>, exact: true},
    // {path: "/buildings", element: <Buildings/>, exact: true},
    // {path: "/performances", element: <Performances/>, exact: true},
    // {path: "/contests", element: <ContestsDistribution/>, exact: true},
    // {path: "/error", element: <Error/>, exact: true},
    {path: "*", element: <Login/>, exact: true},
]