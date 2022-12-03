import axios from 'axios';

let accessToken: any = '';
if (typeof window !== 'undefined') {
  accessToken = localStorage.getItem('accessToken');
}

export const client = axios.create({
  headers: {
    withCredentials: true,
    Authorization: accessToken,
    'Content-Type': `application/json`,
    'ngrok-skip-browser-warning': '111',
  },
});

const requestArray: any = [];

client.interceptors.request.use(
  async (config) => {
    const accessToken = localStorage.getItem('accessToken');
    config.headers = {
      withCredentials: true,
      Authorization: `${accessToken}`,
      'Content-Type': `application/json`,
      'ngrok-skip-browser-warning': '111',
    };
    return config;
  },
  (error) => {
    Promise.reject(error);
  },
);

client.interceptors.response.use(
  (response) => {
    if (requestArray.length != 0) {
      requestArray.forEach(function (x: any, i: any) {
        if (response.config.url == x.url) {
          requestArray.splice(i, 1);
        }
      });
    }
    return response;
  },
  async (error) => {
    const {
      config,
      response: { status },
    } = error;
    if (status === 401) {
      const originalRequest = config;
      const { data } = await axios.put(
        `/api/auth/token`,
        {},
        {
          headers: {
            RefreshToken: localStorage.getItem('refreshToken'),
            'ngrok-skip-browser-warning': '111',
          },
        },
      );
      const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
        data;
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
      if (requestArray.length !== 0) {
        requestArray.forEach((x: any) => {
          try {
            x.headers.Authorization = newAccessToken;
            client.defaults.headers.common['Authorization'] = newAccessToken;
          } catch (err) {
            console.log(err);
          }
        });
      }
      return client(originalRequest);
    }
    return Promise.reject(error);
  },
);
