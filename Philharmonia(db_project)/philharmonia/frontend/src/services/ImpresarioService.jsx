import axios from "axios";

const IMPRESARIO_BASE_API_URL = "http://localhost:8080/api/v1/impresarios";
export async function getAll() {
    const response = await axios.get(IMPRESARIO_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function getImpresariosByArtistId(id) {
    const path = IMPRESARIO_BASE_API_URL + "/artists/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}
