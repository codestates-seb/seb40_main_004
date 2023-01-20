import { atom } from 'recoil';
import { IDataHeader } from '../types/user';
import { UserDashboard } from '../types/dashboard';

export const userEmailAtom = atom({
  key: 'email',
  default: '',
});

export const userAuthKeyAtom = atom({
  key: 'auth',
  default: '',
});

export const userPasswordAtom = atom({
  key: 'password',
  default: '',
});

export const userNickNameAtom = atom({
  key: 'nickName',
  default: '',
});

export const userDashboardAtom = atom<UserDashboard>({
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

// 현재 로그인된 유저의 포인트를 포함한 모든 정보
export const dataHeaderAtom = atom<IDataHeader | null>({
  key: 'dataHeader',
  default: null,
});
