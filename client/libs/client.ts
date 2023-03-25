import axios from 'axios';
import { getAccessToken, getRefreshToken, setTokens } from './tokens';

// 액세스 토큰을 갱신하는 로직
export const refreshAccessToken = async () => {
  const refreshToken = getRefreshToken();

  try {
    const res = await axios.put(
      `/api/auth/token`,
      {},
      {
        headers: {
          RefreshToken: refreshToken,
          'ngrok-skip-browser-warning': '111',
        },
      },
    );

    const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
      res.data;
    setTokens(newAccessToken, newRefreshToken);

    return { newAccessToken, newRefreshToken };
  } catch (error: any) {
    // 리프레시 토큰의 권한이 없거나, 리프레시 토큰이 유효하지 않은 경우
    // 로그인 페이지로 이동합니다.
    if (
      (error.response && error.response.status === 401) ||
      error.response.status === 404
    ) {
      localStorage.clear();
      window.location.href = '/login';
      throw new Error('Refresh token이 유효하지 않습니다.');
    } else {
      throw error;
    }
  }
};
// 액세스 토큰을 심어서 요청을 보낸다.
export const client = axios.create({
  headers: {
    withCredentials: true,
  },
});

// request 요청 가로채어 액세스 토큰 심어서 보내준다.
client.interceptors.request.use(
  async (config) => {
    const accessToken = getAccessToken();
    config.headers = {
      withCredentials: true,
      Authorization: accessToken,
      'Content-Type': `application/json`,
      'ngrok-skip-browser-warning': '111',
    };
    return config;
  },
  (error) => {
    Promise.reject(error);
  },
);

// response 요청 가로챈다.
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
    // 응답에 대한 에러 코드가 401인 경우 - 인증이 되지 않았으므로 갱신 필요
    if (status === 401) {
      // 액세스 토큰을 갱신하는 요청을 보내고 그 결과값을 저장.
      const { newAccessToken } = await refreshAccessToken();
      // put 요청 이후 새로운 요청 config 생성
      const modifiedConfig = {
        ...config,
        headers: {
          ...config.headers,
          // 새로운 액세스 토큰으로 교체한다.
          Authorization: newAccessToken,
        },
      };
      // 변경된 새로운 요청 config로 변경하여 다시 요청한다.
      return client(modifiedConfig);
    }
    return Promise.reject(error);
  },
);
