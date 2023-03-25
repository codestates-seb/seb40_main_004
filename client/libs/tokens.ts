export const getAccessToken = () => {
  let accessToken: string | null = '';
  if (typeof window !== 'undefined') {
    accessToken = localStorage.getItem('accessToken');
  }

  return accessToken;
};

export const getRefreshToken = () => {
  let accessToken: string | null = '';
  if (typeof window !== 'undefined') {
    accessToken = localStorage.getItem('refreshToken');
  }

  return accessToken;
};

export const setTokens = (newAccessToken: string, newRefreshToken?: string) => {
  localStorage.setItem('accessToken', newAccessToken);

  if (newRefreshToken) {
    localStorage.setItem('refreshToken', newRefreshToken);
  }
};
