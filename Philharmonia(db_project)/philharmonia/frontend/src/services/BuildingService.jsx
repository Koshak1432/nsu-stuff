import axios from "axios";


const BUILDING_BASE_API_URL = "http://localhost:8080/api/v1/buildings"
export async function getAllBuildings() {
    const response = await axios.get(BUILDING_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}


export async function addBuilding(building) {
    console.log(building);
    const response = await axios.post(BUILDING_BASE_API_URL, building).catch(error => console.error(error));
    return response.data;
}

export async function updateBuilding(building) {
    const response = await axios.put(BUILDING_BASE_API_URL, building).catch(error => console.error(error));
    return response.data;
}

export async function deleteBuilding(id) {
    const path = BUILDING_BASE_API_URL + "/" + id;
    const response = await axios.delete(path).catch(error => console.error(error));
    return response.data;
}

export async function getTheater(id) {
    const path = BUILDING_BASE_API_URL + "/theaters/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function getAllTheaters() {
    const path = BUILDING_BASE_API_URL + "/theaters";
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function addTheater(theater) {
    const path = BUILDING_BASE_API_URL + "/theaters";
    const response = await axios.post(path, theater).catch(error => console.error(error));
    return response.data;
}

export async function updateTheater(theater) {
    const path = BUILDING_BASE_API_URL + "/theaters";
    const response = await axios.put(path, theater).catch(error => console.error(error));
    return response.data;
}

export async function getAllPalaces() {
    const path = BUILDING_BASE_API_URL + "/palaces";
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}
export async function addPalace(palace) {
    const path = BUILDING_BASE_API_URL + "/palaces";
    const response = await axios.post(path, palace).catch(error => console.error(error));
    return response.data;
}

export async function updatePalace(palace) {
    const path = BUILDING_BASE_API_URL + "/palaces";
    const response = await axios.put(path, palace).catch(error => console.error(error));
    return response.data;
}

export async function getAllTypes() {
    const path = BUILDING_BASE_API_URL + "/types";
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}
