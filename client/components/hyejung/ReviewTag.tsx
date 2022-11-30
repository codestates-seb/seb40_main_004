/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-30
 */
import React, { useState } from 'react';
import { useRecoilState } from 'recoil';
import { reviewTagsAtom } from '../../atomsHJ';

type TagProps = {
  children: string;
  isSelectable: boolean;
  enumTag: string;
};

export const ReviewTag = ({ children, isSelectable, enumTag }: TagProps) => {
  const [isSelected, setIsSelected] = useState(false);
  const [reviewTags, setReviewTags] = useRecoilState(reviewTagsAtom);

  // 클릭시 selected state 와 selectedTags state를 업데이트
  const onClick = () => {
    setIsSelected(!isSelected);
    if (!isSelected) {
      const newState = reviewTags.slice();
      newState.push({ badgeId: 0, name: enumTag });
      setReviewTags(newState);
    } else {
      setReviewTags(reviewTags.filter((tag) => tag.name !== enumTag));
    }
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
