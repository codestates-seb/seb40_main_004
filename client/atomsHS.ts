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

export const categoryAtom = atom({
  key: 'category',
  default: 'QNA',
});

export const tagIdAtom = atom({
  key: 'tagId',
  default: 0,
});
