import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookmark as SolidBookmark } from '@fortawesome/free-solid-svg-icons';
import { faBookmark as VoidBookmark } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { useFetch } from '@libs/useFetchSWR';
import { useHandleClickBookmark } from './useHandleClickBookmark';

export const BtnBookmark = () => {
  const router = useRouter();
  const { articleId } = router.query;

  const { data } = useFetch(`/api/articles/${articleId}`);
  const [isMarked, setIsMarked] = useState(data.isBookmarked);

  const { handleClickBookmark } = useHandleClickBookmark(articleId as string);

  return (
    <>
      <button onClick={() => handleClickBookmark(setIsMarked)}>
        {isMarked ? (
          <FontAwesomeIcon
            icon={SolidBookmark}
            style={{ color: '#A0A0A0' }}
            className="fa-xl"
          />
        ) : (
          <FontAwesomeIcon
            icon={VoidBookmark}
            className="fa-xl"
            style={{ color: '#A0A0A0' }}
          />
        )}
      </button>
    </>
  );
};
