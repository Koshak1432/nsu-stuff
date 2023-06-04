import axios from "axios";

const SPONSOR_BASE_API_URL = "http://localhost:8080/api/v1/sponsors"
export async function getAllSponsors() {
    const response = await axios.get(SPONSOR_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function addSponsor(sponsor) {
    const response = await axios.post(SPONSOR_BASE_API_URL, sponsor).catch(error => console.error(error));
    return response.data;
}

export async function updateSponsor(sponsor) {
    const response = await axios.put(SPONSOR_BASE_API_URL, sponsor).catch(error => console.error(error));
    return response.data;
}

export async function deleteSponsor(id) {
    const path = SPONSOR_BASE_API_URL + "/" + id;
    const response = await axios.delete(path).catch(error => console.error(error));
    return response.data;
}