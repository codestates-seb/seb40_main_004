import { atom } from 'recoil';

// 답변 작성 후 스크롤 상단 이동시에 활용
export const isAnswerPostedAtom = atom({
  key: 'isAnswerPosted',
  default: false,
});

// 답변 작성 후 수정 상단 이동시에 활용
export const isAnswerEditAtom = atom({
  key: 'isAnswerEdit',
  default: {
    isEdit: false,
    answerId: 0,
    answerPage: 0,
    payload: '',
  },
});
