import axios from "axios";

const GENRES_BASE_API_URL = "http://localhost:8080/api/v1/genres";
export async function getAllGenres() {
    const response = await axios.get(GENRES_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function getGenresByArtistId(id) {
    const path = GENRES_BASE_API_URL + "/artists/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}
