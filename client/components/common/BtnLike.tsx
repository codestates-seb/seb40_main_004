import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as SolidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as VoidHeart } from '@fortawesome/free-regular-svg-icons';
import { useLayoutEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';

import { client } from '@libs/client';
import { isLoginAtom } from '@atoms/loginAtom';
import { useCheckClickIsLogin } from '@libs/useCheckIsLogin';
import { useFetch } from '@libs/useFetchSWR';
import { toast } from 'react-toastify';

type BtnLikeProps = {
  answerId?: number;
  isLikedInit?: boolean;
  likeCntInit?: number;
};

export const BtnLike = ({
  answerId,
  isLikedInit,
  likeCntInit,
}: BtnLikeProps) => {
  const router = useRouter();
  const { articleId } = router.query;

  const isLogin = useRecoilValue(isLoginAtom);
  const checkIsLogin = useCheckClickIsLogin();

  const url = answerId
    ? `/api/articles/${articleId}/answers/${answerId}`
    : `/api/articles/${articleId}`;

  const data = answerId || useFetch(url).data;

  const [isLiked, setIsLiked] = useState(data?.isLiked);
  const [likeCnt, setLikeCnt] = useState(data?.likes);

  useLayoutEffect(() => {
    if (answerId) {
      setIsLiked(isLikedInit as boolean);
      setLikeCnt(likeCntInit as number);
    }
  }, [answerId]);

  const onClickLike = async () => {
    if (isLogin) {
      try {
        const res = await client.post(`${url}/likes`);
        setIsLiked(res.data.isLiked);
        setLikeCnt(res.data.likeCount);
      } catch (err) {
        toast.error('실패하였습니다! 다시 시도해주세요.');
      }
    } else {
      checkIsLogin();
    }
  };

  return (
    <div className="flex space-x-1">
      <button onClick={onClickLike}>
        {isLiked ? (
          <FontAwesomeIcon icon={SolidHeart} style={{ color: '#FF9F10' }} />
        ) : (
          <FontAwesomeIcon icon={VoidHeart} />
        )}
      </button>
      <span className="text-xl pr-3">{likeCnt}</span>
    </div>
  );
};
