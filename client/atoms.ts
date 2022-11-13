import { atom } from 'recoil';

export const isLoginedAtom = atom({
  key: 'isLogined',
  default: false,
});
