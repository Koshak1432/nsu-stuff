import axios from "axios";


const ARTIST_BASE_API_URL = "http://localhost:8080/api/v1/artists";
export async function getAllArtists() {
    const response = await axios.get(ARTIST_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function getArtistById(id) {
    const path = ARTIST_BASE_API_URL + "/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function getArtistByContestId(id) {
    const path = ARTIST_BASE_API_URL + "/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function postArtist(newArtist) {
    const response = await axios.post(ARTIST_BASE_API_URL, newArtist).catch(error => console.error(error));
    return response.data;
}

export async function updateArtist(updArtist) {
    const path = ARTIST_BASE_API_URL + "/" + updArtist.id;
    const response = await axios.put(path, updArtist).catch(error => console.error(error));
    return response.data;
}

export async function deleteArtist(id) {
    const path = ARTIST_BASE_API_URL + "/" + id;

    const response = await axios.delete(path, id).catch(error => console.error(error));
    return response.data;
}

// artist to genres
export async function addArtistToGenre(pair) {
    const path = ARTIST_BASE_API_URL + "/distribution";
    const response = await axios.post(path, pair).catch(error => console.error(error));
    return response.data;
}

export async function getArtistToGenres() {
    const path = ARTIST_BASE_API_URL + "/distribution";
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function deleteArtistToGenre(pair) {
    const path = ARTIST_BASE_API_URL + "/distribution";
    const response = await axios.delete(path, pair).catch(error => console.error(error));
    return response.data;
}


