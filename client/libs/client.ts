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

client.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const {
      config,
      response: { status },
    } = error;
    if (status === 401) {
      if (error.response.data.message === 'TokenExpiredError') {
        const originalRequest = config;
        const refreshToken = localStorage.getItem('refreshToken');

        const { data } = await client.put(`/api/auth/token`, {
          headers: {
            RefreshToken: refreshToken,
          },
        });
        const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
          data;
        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        axios.defaults.headers.common.Authorization = `Bearer ${newAccessToken}`;
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return axios(originalRequest);
      }
    }
    return Promise.reject(error);
  },
);
