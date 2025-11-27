import axios from "axios";

const API_URL = "http://localhost:8080/api";

const axiosInstance = axios.create({ baseURL: API_URL });

// Add JWT token to requests if exists
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log("Token being sent:", token.substring(0, 20) + "..."); // Debug log
        } else {
            console.warn("No token found in localStorage!");
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor for better error handling
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 403) {
            console.error("403 Forbidden - Token might be invalid or expired");
            console.error("Response data:", error.response.data);
        }
        return Promise.reject(error);
    }
);

export const register = (user) => axiosInstance.post("/auth/register", user);
export const login = (user) => axiosInstance.post("/auth/login", user);
export const getProjects = () => axiosInstance.get("/projects/all");
export const createProject = (project) => axiosInstance.post("/projects", project);
export const joinProject = (projectId) => axiosInstance.post(`/projects/${projectId}/join`);
export const getMessages = (projectId) => axiosInstance.get(`/chat/projects/${projectId}/messages`);
export const sendMessage = (projectId, content) => axiosInstance.post(`/chat/projects/${projectId}/messages`, { content });
export const getProjectById = (projectId) => axiosInstance.get(`/projects/${projectId}`);
export const getMyProjects = () => axiosInstance.get("/projects/my-projects");
export const kickMember = (projectId, userId) => axiosInstance.delete(`/projects/${projectId}/members/${userId}`);