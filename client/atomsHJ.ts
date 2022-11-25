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

export const isAnswerPostAtom = atom({
  key: 'isAnswerPost',
  default: {
    isAnswerPost: false,
    answerId: '',
  },
});

export const articleAuthorIdAtom = atom({
  key: 'articleAuthorId',
  default: '',
});
