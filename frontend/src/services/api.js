import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const fetchDeveloperInfo = async () => {
  const { data } = await api.get('/developer-info');
  return data;
};

export const fetchProbeStatus = async () => {
  const { data } = await api.get('/probe');
  return data;
};

export const updateProbeStatus = async (payload) => {
  const { data } = await api.post('/probe', payload);
  return data;
};

export const fetchUsers = async (nameFilter = '') => {
  const { data } = await api.get('/users', {
    params: nameFilter ? { name: nameFilter } : undefined
  });
  return data;
};

export const fetchUser = async (id) => {
  const { data } = await api.get(`/users/${id}`);
  return data;
};

export const createUser = async (user) => {
  const { data } = await api.post('/users', user);
  return data;
};

export const updateUser = async (id, user) => {
  const { data } = await api.put(`/users/${id}`, user);
  return data;
};

export const deleteUser = async (id) => {
  await api.delete(`/users/${id}`);
};

export default api;
