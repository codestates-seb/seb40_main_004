import React, { useState } from 'react';
import { useRecoilState } from 'recoil';
import { selectedTagsAtom } from '../../atomsHJ';

type TagProps = {
  children: string;
  isSelectable: boolean;
};

export const ReviewTag = ({ children, isSelectable }: TagProps) => {
  // 현재 리뷰 태그가 선택되었는지 여부를 확인하는 state
  const [isSelected, setIsSelected] = useState(false);
  // 선택된 리뷰 배열이 저장되는 전역 state
  const [selectedTags, setSelectedTags] = useRecoilState(selectedTagsAtom);

  // 클릭시 selected state 와 selectedTags state를 업데이트
  const onClick = () => {
    setIsSelected(!isSelected);
    !isSelected
      ? setSelectedTags([children, ...selectedTags])
      : setSelectedTags(selectedTags.filter((tag) => tag !== children));
  };
  return (
    <div className="p-4 lg:p-9 md:text-lg font-bold">
      {!isSelected && !isSelectable ? (
        <span className="text-main-gray transition-all">{children}</span>
      ) : (
        <button
          className={
            isSelected
              ? ' bg-main-yellow rounded-full px-4 py-3 transition-all text-white'
              : ` font-bold bg-none transition-all`
          }
          key={children}
          onClick={onClick}
        >
          <span>{children}</span>
        </button>
      )}
    </div>
  );
};
