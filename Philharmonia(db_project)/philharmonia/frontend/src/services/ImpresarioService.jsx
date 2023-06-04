import axios from "axios";

const IMPRESARIO_BASE_API_URL = "http://localhost:8080/api/v1/impresarios";
export async function getAllImpresarios() {
    const response = await axios.get(IMPRESARIO_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function getImpresariosByArtistId(id) {
    const path = IMPRESARIO_BASE_API_URL + "/by-artist/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function getArtistsByImpresarioId(id) {
    const path = IMPRESARIO_BASE_API_URL + "/" + id + "/artists";
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function addImpresario(impresario) {
    const response = await axios.post(IMPRESARIO_BASE_API_URL, impresario).catch(error => console.error(error));
    return response.data;
}

export async function updateImpresario(impresario) {
    const response = await axios.put(IMPRESARIO_BASE_API_URL, impresario).catch(error => console.error(error));
    return response.data;
}

export async function deleteImpresario(id) {
    const path = IMPRESARIO_BASE_API_URL + "/" + id;
    const response = await axios.delete(path).catch(error => console.error(error));
    return response.data;
}
