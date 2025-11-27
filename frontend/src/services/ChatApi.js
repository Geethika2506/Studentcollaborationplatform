import axios from "axios";

// Backend URL
const BASE_URL = "http://localhost:8080/api/chat";

export const getProjectMessages = async (projectId) => {
  try {
    const response = await axios.get(`${BASE_URL}/projects/${projectId}/messages`);
    return response.data;
  } catch (error) {
    console.error("Error fetching messages:", error);
    return [];
  }
};

export const sendMessage = async (projectId, content) => {
  try {
    const response = await axios.post(`${BASE_URL}/projects/${projectId}/messages`, { content });
    return response.data;
  } catch (error) {
    console.error("Error sending message:", error);
    return null;
  }
};

