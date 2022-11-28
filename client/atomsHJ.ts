import { atom } from 'recoil';

// 채택/후원 관련 atoms 시작

export const selectedTagsAtom = atom({
  key: 'selectedTags',
  default: [''],
});

export const reviewMsgAtom = atom({
  key: 'reviewMsg',
  default: '',
});

export const supportMorakAtom = atom({
  key: 'supportMorak',
  default: '',
});

export const supportPayloadAtom = atom({
  key: 'supportPayload',
  default: {
    selectedTags: [''],
    message: '',
    morak: '',
  },
});

// 채택/후원 관련 atoms 끝

export const isAnswerPostedAtom = atom({
  key: 'isAnswerPosted',
  default: false,
});

export const articleAuthorIdAtom = atom({
  key: 'articleAuthorId',
  default: '',
});

export const isAnswerEditAtom = atom({
  key: 'isAnswerEdit',
  default: {
    isEdit: false,
    answerId: 0,
    answerPage: 0,
    payload: '',
  },
});

export const isCommentOpenAtom = atom({
  key: 'isCommentOpen',
  default: false,
});
