import { atom } from 'recoil';
import { userDashboard } from './interfaces';

export const isLoginAtom = atom({
  key: 'isLogin',
  default: false,
});

export const userDashboardAtom = atom<userDashboard>({
  key: 'userDashboard',
  default: {
    userId: 0,
    email: '',
    nickname: '',
    jobType: '',
    grade: '',
    point: 0,
    github: '',
    blog: '',
    infoMessage: '',
    rank: 0,
    avatar: {
      avatarId: 0,
      filename: '',
      remotePath: '',
    },
    tags: [],
    reviewBadges: [],
    articles: [], // 수정 필요
    activities: [], // 수정 필요
    reviews: [],
  },
});

export const avatarPathAtom = atom({
  key: 'avatarPath',
  default: '/favicon.ico',
});
