import { atom } from 'recoil';

// 렌더링 여부 확인
export const renderingAtom = atom({
  key: 'rendering',
  default: true,
});
