import axios from "axios";


const ARTIST_BASE_API_URL = "http://localhost:8080/api/v1/artists";
export async function getAll() {
    const response = await axios.get(ARTIST_BASE_API_URL);
    return response.data;
}