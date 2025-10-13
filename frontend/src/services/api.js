import axios from 'axios';

const DEFAULT_BASE_PATH = '/api';
const LOCAL_HOSTNAMES = new Set(['localhost', '127.0.0.1', '::1']);

const resolveBaseURL = () => {
  const envValue = import.meta.env.VITE_API_BASE_URL;

  if (typeof window === 'undefined') {
    return envValue || DEFAULT_BASE_PATH;
  }

  if (!envValue) {
    return DEFAULT_BASE_PATH;
  }

  try {
    const parsed = new URL(envValue, window.location.origin);
    const envIsAbsolute = /^[a-zA-Z][a-zA-Z\d+\-.]*:/.test(envValue);
    const sameOrigin = parsed.origin === window.location.origin;
    const runningLocally = LOCAL_HOSTNAMES.has(window.location.hostname);

    if (envIsAbsolute) {
      return !sameOrigin && runningLocally ? DEFAULT_BASE_PATH : envValue;
    }

    const path = parsed.pathname.startsWith('/') ? parsed.pathname : `/${parsed.pathname}`;
    return path || DEFAULT_BASE_PATH;
  } catch {
    return DEFAULT_BASE_PATH;
  }
};

const api = axios.create({
  baseURL: resolveBaseURL(),
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
