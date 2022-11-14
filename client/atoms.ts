import { atom } from 'recoil';

export const isLoggedInAtom = atom({
  key: 'isLoggedIn',
  default: false,
});
