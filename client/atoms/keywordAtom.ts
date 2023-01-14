import { atom } from 'recoil';

// 네비바 검색 관련 전역상태
export const keywordAtom = atom({
  key: 'keyword',
  default: '',
});
