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
