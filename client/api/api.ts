import axios from 'axios';

export const authentiCate = async (email: string, password: string) => {
  const res = await axios.post(`/api/auth/token`, { email, password });
  return res;
};

export const authCheckCode = async (
  email: string,
  authKey: string | undefined,
) => {
  return await axios.post(`/api/auth/password/recovery`, {
    email,
    authKey,
  });
};

export const authGetCode = async (email: string) => {
  return await axios.post(`/api/auth/password/support`, {
    email,
  });
};

export const signUpWithEmail = async (
  email: string,
  password: string,
  confirmPassword: string,
  nickname: string,
) => {
  return await axios.post(`/api/auth/mail`, {
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
  return await axios.post(`/api/auth`, {
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
