import { Tags } from '@type/article';
import { BtnTag } from './BtnTag';

type TagListProps = {
  tags?: Tags[];
};

export const TagList = ({ tags }: TagListProps) => {
  return (
    <article className="space-x-1 sm:space-x-3">
      {tags?.map((tag) => (
        <BtnTag key={tag.tagId}>{tag.name}</BtnTag>
      ))}
    </article>
  );
};
