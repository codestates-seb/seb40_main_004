import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookmark as SolidBookmark } from '@fortawesome/free-solid-svg-icons';
import { faBookmark as VoidBookmark } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { client } from '../../../../libs/client';
import { useCheckClickIsLogin } from '../../../../libs/useCheckIsLogin';
import { isLoginAtom } from '../../../../atomsYW';
import { useRecoilValue } from 'recoil';

type BtnBookmarkProps = {
  isBookmarked: boolean;
};
export const BtnBookmark = ({ isBookmarked }: BtnBookmarkProps) => {
  const router = useRouter();
  const { articleId } = router.query;
  const isLogin = useRecoilValue(isLoginAtom);
  const checkIsLogin = useCheckClickIsLogin();

  const [isMarked, setIsMarked] = useState(isBookmarked);

  const onClickBookmark = () => {
    if (isLogin) {
      client.post(`/api/articles/${articleId}/bookmark`).then((res) => {
        setIsMarked(res.data.scrappedByThisUser);
      });
    } else {
      checkIsLogin();
    }
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
