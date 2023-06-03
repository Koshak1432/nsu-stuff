import axios from "axios";

const PERFORMANCE_BASE_API_URL = "http://localhost:8080/api/v1/performances"
export async function getAllPerformances() {
    const response = await axios.get(PERFORMANCE_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}


