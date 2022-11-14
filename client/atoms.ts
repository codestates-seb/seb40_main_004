import { atom } from 'recoil';

export const isLoggedInAtom = atom({
  key: 'isLogined',
  default: false,
});
