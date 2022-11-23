/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-21
 */
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookmark as SolidBookmark } from '@fortawesome/free-solid-svg-icons';
import { faBookmark as VoidBookmark } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { client } from '../../libs/client';

type BtnBookmarkProps = {
  isBookmarked: boolean;
};
export const BtnBookmark = ({ isBookmarked }: BtnBookmarkProps) => {
  const router = useRouter();
  const { articleId } = router.query;

  const [isMarked, setIsMarked] = useState(isBookmarked);

  const onClickBookmark = () => {
    client
      .post(`/articles/${articleId}/bookmarks`)
      .then((res) => setIsMarked(res.data.scrappedByThisUser))
      .catch((err) => {
        alert('로그인이 필요한 서비스입니다.');
        console.error(err);
      });
  };

  return (
    <>
      <button onClick={onClickBookmark}>
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
