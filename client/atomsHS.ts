import { atom } from 'recoil';

export const userEmailAtom = atom({
  key: 'email',
  default: '',
});

export const userAuthKey = atom({
  key: 'auth',
  default: '',
});

export const userPassword = atom({
  key: 'password',
  default: '',
});

export const userNickName = atom({
  key: 'nickName',
  default: '',
});

export const accessTokenAtom = atom({
  key: 'access_token',
  default: {},
});

export const refreshTokenAtom = atom({
  key: 'refresh_token',
  default: {},
});
