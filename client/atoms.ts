import { atom } from 'recoil';

export const isLoggedInAtom = atom({
  key: 'isLogedIn',
  default: false,
});
