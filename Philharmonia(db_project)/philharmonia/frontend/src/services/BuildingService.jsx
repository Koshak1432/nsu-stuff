import axios from "axios";


const BUILDING_BASE_API_URL = "http://localhost:8080/api/v1/buildings"
export async function getAllBuildings() {
    const response = await axios.get(BUILDING_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}