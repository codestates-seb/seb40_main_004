import { elapsedTime } from '@libs/elapsedTime';

type CreatedDateProps = {
  createdAt: string;
};

export const CreatedDate = ({ createdAt }: CreatedDateProps) => {
  return (
    <time className="text-main-gray text-xs">{elapsedTime(createdAt)}</time>
  );
};
