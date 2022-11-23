/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-21
 */
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as SolidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as VoidHeart } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { client } from '../../libs/client';

type BtnLikeProps = {
  isLiked: boolean;
  answerId?: string;
};
export const BtnLike = ({ isLiked, answerId }: BtnLikeProps) => {
  // 좋아요 요청을 위한 articleId
  const router = useRouter();
  const { articleId } = router.query;

  const [isLike, setIsLike] = useState(isLiked);

  const onClickLike = () => {
    console.log(articleId);
    let url = '';
    if (answerId) {
      url = `/articles/${articleId}/answers/${answerId}/likes`;
    } else {
      url = `/articles/${articleId}/likes`;
    }
    console.log(url);
    client
      .post(url)
      .then((res) => setIsLike(res.data.isLiked))
      .catch((err) => {
        console.error(err);
        alert('로그인이 필요한 서비스입니다.');
      });
  };

  return (
    <>
      <button onClick={onClickLike}>
        {isLike ? (
          <FontAwesomeIcon
            icon={SolidHeart}
            style={{ color: '#FF9F10' }}
            className="fa-xl"
          />
        ) : (
          <FontAwesomeIcon icon={VoidHeart} className="fa-xl" />
        )}
      </button>
    </>
  );
};
