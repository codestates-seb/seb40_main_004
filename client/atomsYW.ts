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
    tags: [{ name: '', tag_Id: 0, ranking: 0 }],
    reviewBadges: [{ name: '', badge_Id: 0 }],
    articles: [
      {
        articleId: 0,
        category: '',
        title: '',
        clicks: 0,
        likes: 0,
        isClosed: false,
        tags: [
          {
            tagId: 0,
            name: '',
          },
          {
            tagId: 0,
            name: '',
          },
        ],
        commentCount: 0,
        answerCount: 0,
        createdAt: '',
        lastModifiedAt: '',
        userInfo: {
          userId: 0,
          nickname: '',
          grade: '',
        },
        avatar: {
          avatarId: 0,
          filename: '',
          remotePath: '',
        },
      },
    ], // 수정 필요
    activities: [
      {
        articleCount: 0,
        answerCount: 0,
        commentCount: 0,
        total: 0,
        createdDate: '',
      },
    ], // 수정 필요
    reviews: [
      {
        reviewId: 0,
        content: '',
        createdAt: '',
        userInfo: {
          userId: 0,
          nickname: '',
          grade: '',
        },
      },
    ],
  },
});

export const avatarPathAtom = atom({
  key: 'avatarPath',
  default: '/favicon.ico',
});
