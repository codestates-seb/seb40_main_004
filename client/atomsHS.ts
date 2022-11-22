import { atom } from 'recoil';

export const userEmailAtom = atom({
  key: 'email', //이메일에 대한 키 값
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
