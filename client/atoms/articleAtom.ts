import { atom } from 'recoil';

// 게시글 작성자 id를 저장한 후 다른 컴포넌트에서 작성자 일치 여부 확인시에 활용
export const articleAuthorIdAtom = atom({
  key: 'articleAuthorId',
  default: '',
});

// 게시글 수정시 게시글 수정 페이지로 이동할 때 사용
export const isArticleEditAtom = atom({
  key: 'isArticleEdit',
  default: {
    isArticleEdit: false,
    title: '',
    content: '',
    articleId: '',
  },
});
