type TagListProps = {
  tags: string[];
};

import { TagButton } from '../BtnTag';

export const TagList = ({ tags }: TagListProps) => {
  return (
    <article className="space-x-1 sm:space-x-3">
      {tags.map((el) => (
        <TagButton key={el}>{el}</TagButton>
      ))}
    </article>
  );
};
