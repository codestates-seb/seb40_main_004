import { atom } from 'recoil';

// 채택/후원 관련 atoms 시작
export const reviewTagsEnumAtom = atom({
  key: 'reviewTagsEnum',
  default: [
    ['친절한', 'KINDLY'],
    ['현명한', 'WISE'],
    ['똑똑한', 'SMART'],
    ['응가', 'POO'],
    ['꼼꼼한', 'DETAILED'],
    ['박식한', 'INTELLEGENT'],
    ['너그러운', 'GENEROUS'],
    ['믿음직한', 'COHERENT'],
    ['재치있는', 'WITTY'],
    ['도움이 되는', 'HELPFUL'],
    ['따듯한', 'WARM'],
    ['사려 깊은', 'CONSIDERATE'],
    ['문제를 해결하는', 'PROBLEMSOLVER'],
    ['배려심 있는', 'CARING'],
    ['창의적인', 'CREATIVE'],
    ['논리적인', 'LOGICAL'],
    ['이해하기 쉬운', 'UNDERSTANDABLE'],
  ],
});

export const reviewTagsAtom = atom({
  key: 'reviewTags',
  default: [{ badgeId: 0, name: '' }],
});

export const reviewContentAtom = atom({
  key: 'reviewContent',
  default: '',
});

export const reviewPointAtom = atom({
  key: 'reviewPoint',
  default: 0,
});

export const reviewRequestAtom = atom({
  key: 'reviewRequest',
  default: {
    targetId: -1,
    articleId: '',
    targetUserName: '',
    dashboardUrl: '',
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

export const currPagesIndexAtom = atom({
  key: 'currPagesIndex',
  default: 0,
});
