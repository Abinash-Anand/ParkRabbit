import httpClient from "./httpClient";

export const login = async (payload) => {
  return httpClient.post("/api/auth/login", payload);
};

export const signup = async (payload) => {
  return httpClient.post("/api/auth/signup", payload);
};
