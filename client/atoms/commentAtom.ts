import { atom } from 'recoil';

// 댓글 더보기
export const isCommentOpenAtom = atom({
  key: 'isCommentOpen',
  default: false,
});
