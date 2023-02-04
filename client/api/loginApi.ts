import { makePostRequest } from './makePostRequest';

export const authentiCate = async (email: string, password: string) => {
  const res = makePostRequest(`/api/auth/token`, { email, password });
  return res;
};
