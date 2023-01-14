/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-29
 */
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as SolidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as VoidHeart } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { client } from '../../libs/client';
import { useCheckClickIsLogin } from '../../libs/useCheckIsLogin';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../atoms/loginAtom';

type BtnLikeProps = {
  isLiked: boolean;
  answerId?: number;
  likes: number;
};
export const BtnLike = ({ isLiked, answerId, likes }: BtnLikeProps) => {
  // 좋아요 요청을 위한 articleId
  const router = useRouter();
  const { articleId } = router.query;
  const isLogin = useRecoilValue(isLoginAtom);
  const checkIsLogin = useCheckClickIsLogin();

  const [isLike, setIsLike] = useState(isLiked);
  const [likeCnt, setLikeCnt] = useState(likes);

  const url = answerId
    ? `/api/articles/${articleId}/answers/${answerId}/likes`
    : `/api/articles/${articleId}/likes`;

  const onClickLike = async () => {
    if (isLogin) {
      client
        .post(url)
        .then((res) => {
          setIsLike(res.data.isLiked);
          setLikeCnt(res.data.likeCount);
        })
        .catch(() => {
          console.log('fail');
        });
    } else {
      checkIsLogin();
    }
  };

  return (
    <div className="flex space-x-1">
      <button onClick={onClickLike}>
        {isLike ? (
          <FontAwesomeIcon icon={SolidHeart} style={{ color: '#FF9F10' }} />
        ) : (
          <FontAwesomeIcon icon={VoidHeart} />
        )}
      </button>
      <span className="text-xl pr-3">{likeCnt}</span>
    </div>
  );
};
