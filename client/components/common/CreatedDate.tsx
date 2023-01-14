type CreatedDateProps = {
  createdAt: string;
};

import { elapsedTime } from '../../libs/elapsedTime';

export const CreatedDate = ({ createdAt }: CreatedDateProps) => {
  return (
    <time className="text-main-gray text-xs">{elapsedTime(createdAt)}</time>
  );
};
