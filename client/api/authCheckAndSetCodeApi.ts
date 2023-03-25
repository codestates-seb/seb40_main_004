import { makePostRequest } from './makePostRequest';

export const authCheckCode = async (
  email: string,
  authKey: string | undefined,
) => {
  makePostRequest(`/api/auth/password/recovery`, { email, authKey });
};

export const authGetCode = async (email: string) => {
  makePostRequest(`/api/auth/password/support`, { email });
};
