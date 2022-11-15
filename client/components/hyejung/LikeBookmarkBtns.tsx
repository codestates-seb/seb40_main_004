import { BtnLike } from './BtnLike';
import { BtnBookmark } from './BtnBookmark';

export const LikeBookmarkBtns = () => {
  return (
    <div className="flex space-x-1">
      <BtnLike />
      <span className="text-xl pr-3">14</span>
      <BtnBookmark />
    </div>
  );
};
