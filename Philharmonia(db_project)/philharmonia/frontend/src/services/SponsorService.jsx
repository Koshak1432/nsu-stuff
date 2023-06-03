import axios from "axios";

const SPONSOR_BASE_API_URL = "http://localhost:8080/api/v1/sponsors"
export async function getAllSponsors() {
    const response = await axios.get(SPONSOR_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}