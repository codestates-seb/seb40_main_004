import axios from 'axios';
import { getAccessToken, getRefreshToken, setTokens } from './tokens';

const accessToken = getAccessToken();
const refreshToken = getRefreshToken();

export const refreshAccessToken = async () => {
  return axios
    .put(
      `/api/auth/token`,
      {},
      {
        headers: {
          RefreshToken: refreshToken,
          'ngrok-skip-browser-warning': '111',
        },
      },
    )
    .then((res) => {
      const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
        res.data;
      setTokens(newAccessToken, newRefreshToken);

      return { newAccessToken, newRefreshToken };
    })
    .catch((err) => {
      // 리프레시 토큰이 만료가 된 경우
      if (err.response.status === 401) {
        localStorage.clear();
        window.location.href = '/login';
      }
      // 중복 요청으로 갱신 이전 리프레시 토큰이 넘어가는 경우
      else if (err.response.status === 404) {
        window.location.reload();
      }
    });
};

export const client = axios.create({
  headers: {
    withCredentials: true,
    Authorization: accessToken,
    'Content-Type': `application/json`,
    'ngrok-skip-browser-warning': '111',
  },
});

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
    return response;
  },
  // 에러가 있을 경우
  async (error) => {
    const {
      config,
      response: { status },
    } = error;
    // 기존 요청을 저장해둔다.
    const originalRequest = config;
    // 만약 그 에러가 401 에러라면
    if (status === 401 || status === 403) {
      6;
      const response = await refreshAccessToken();
      originalRequest.headers.Authorization = response?.newAccessToken;
      client.defaults.headers.common['Authorization'] =
        response?.newRefreshToken;
      return client(originalRequest);
    }
    return Promise.reject(error);
  },
);

/*
1. 같은 페이지 내에서 새로고침을 할 때는 401 에러가 뜨면 바로 갱신이 되어서 put 요청에서 404 에러가 발생하지 않는다.
2. 다른 페이지로 이동을 하게되면 401 에러가 발생해서 put 요청이 들어가는데, 이 때 404 에러가 발생한다.
3. 그러면 put 요청이 발생했는데 404 에러가 떴다 => 그러면 메시지를 띄우고 새로고침을 해버리자.
*/
