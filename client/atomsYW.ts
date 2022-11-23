import { atom } from 'recoil';
import { curUser } from './components/yeonwoo/BtnUser';

export const curUserAtom = atom<curUser>({
  key: 'curUser',
  default: {
    email: '',
    userId: 0,
    nickname: '',
  },
});
