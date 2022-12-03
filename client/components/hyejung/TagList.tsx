/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-21
 */
type TagListProps = {
  tags: Tags[];
};

import { Tags } from '../../libs/interfaces';
import { BtnTag } from './BtnTag';

export const TagList = ({ tags }: TagListProps) => {
  return (
    <article className="space-x-1 sm:space-x-3">
      {tags.map((tag) => (
        <BtnTag key={tag.tagId}>{tag.name}</BtnTag>
      ))}
    </article>
  );
};
