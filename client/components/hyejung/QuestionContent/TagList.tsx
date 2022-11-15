import { TagButton } from '../TagButton';

export const TagList = () => {
  return (
    <article className="space-x-1 sm:space-x-3">
      {['React', 'React Query', 'Java'].map((el) => (
        <TagButton key={el}>{el}</TagButton>
      ))}
    </article>
  );
};
