import axios from 'axios';
import { makePostRequest } from './makePostRequest';

export const signUpWithEmail = async (
  email: string,
  password: string,
  confirmPassword: string,
  nickname: string,
) => {
  makePostRequest(`/api/auth/mail`, {
    email,
    password,
    confirmPassword,
    nickname,
  });
};

export const signUpWithEmailAndKey = async (
  email: string,
  password: string,
  confirmPassword: string,
  nickname: string,
) => {
  makePostRequest(`/api/auth`, {
    email,
    password,
    confirmPassword,
    nickname,
  });
};

export const authSetKey = async (
  email: string,
  authKey: string | undefined,
) => {
  const res = await axios.put(`/api/auth/mail`, {
    email,
    authKey,
  });
  return res;
};
