import axios from "axios";

const CONTEST_BASE_API_URL = "http://localhost:8080/api/v1/contests";
export async function getAll() {
    const response = await axios.get(CONTEST_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function getContestById(id) {
    const path = CONTEST_BASE_API_URL + "/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}
