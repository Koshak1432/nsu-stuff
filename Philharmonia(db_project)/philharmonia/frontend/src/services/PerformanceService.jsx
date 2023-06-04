import axios from "axios";

const PERFORMANCE_BASE_API_URL = "http://localhost:8080/api/v1/performances"
const CONTEST_BASE_API_URL = PERFORMANCE_BASE_API_URL + "/contests"

export async function getAllPerformances() {
    const response = await axios.get(PERFORMANCE_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function getContestsDistribution() {
    const path = CONTEST_BASE_API_URL + "/distribution"
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function getContestDistributionById(id) {
    const path = CONTEST_BASE_API_URL + "/distribution/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}

export async function getAllContests() {
    const response = await axios.get(CONTEST_BASE_API_URL).catch(error => console.error(error));
    return response.data;
}

export async function addPerformance(performance) {
    const response = await axios.post(PERFORMANCE_BASE_API_URL, performance).catch(error => console.error(error));
    return response.data;
}

export async function updatePerformance(performance) {
    const response = await axios.put(PERFORMANCE_BASE_API_URL, performance).catch(error => console.error(error));
    return response.data;
}

export async function deletePerformance(id) {
    const path = PERFORMANCE_BASE_API_URL + "/" + id;
    const response = await axios.delete(path).catch(error => console.error(error));
    return response.data;
}

export async function addContestDistribution(distribution) {
    const path = CONTEST_BASE_API_URL + "/distribution";
    const response = await axios.post(path, distribution).catch(error => console.error(error));
    return response.data;
}

export async function updateContestDistribution(distribution) {
    const path = CONTEST_BASE_API_URL + "/distribution";
    const response = await axios.put(path, distribution).catch(error => console.error(error));
    return response.data;
}

export async function deleteContestDistribution(id) {
    const path = CONTEST_BASE_API_URL + "/distribution";
    const response = await axios.delete(path, id).catch(error => console.error(error));
    return response.data;
}


//todo imlement in server
export async function getContestById(id) {
    const path = CONTEST_BASE_API_URL + "/" + id;
    const response = await axios.get(path).catch(error => console.error(error));
    return response.data;
}


