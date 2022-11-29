import axios from 'axios';

let accessToken: any = '';
if (typeof window !== 'undefined') {
  accessToken = localStorage.getItem('accessToken');
}

export const client = axios.create({
  headers: {
    withCredentials: true,
    Authorization: `${accessToken}`,
    'Content-Type': `application/json`,
    'ngrok-skip-browser-warning': '111',
  },
});
