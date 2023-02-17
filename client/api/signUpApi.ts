import axios from 'axios';
import { makePostRequest } from './makePostRequest';

export type UserCredentials = {
  email: string;
  password: string;
  confirmPassword?: string;
  authKey?: string;
  nickname: string;
};

export const signUpWithEmail = async (cridentials: UserCredentials) => {
  makePostRequest(`/api/auth/mail`, { cridentials });
};

export const signUpWithEmailAndKey = async (cridentials: UserCredentials) => {
  makePostRequest(`/api/auth`, { cridentials });
};

export const authSetKey = async (
  email: string,
  authKey: string | undefined,
) => {
  const res = await axios.put(`/api/auth/mail`, { email, authKey });
  return res;
};
