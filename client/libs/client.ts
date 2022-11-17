import axios from 'axios';

export const client = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': `application/json`,
  },
});
