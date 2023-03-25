import { isLoginAtom } from '@atoms/loginAtom';
import { client } from '@libs/client';
import { toast } from 'react-toastify';
import { useRecoilValue } from 'recoil';
import { useCheckClickIsLogin } from '@libs/useCheckIsLogin';
import React, { SetStateAction } from 'react';

export const useHandleClickBookmark = (articleId: string) => {
  const isLogin = useRecoilValue(isLoginAtom);
  const checkIsLogin = useCheckClickIsLogin();

  const handleClickBookmark = async (
    setIsMarked: React.Dispatch<SetStateAction<string>>,
  ) => {
    if (isLogin) {
      try {
        const res = await client.post(`/api/articles/${articleId}/bookmark`);
        setIsMarked(res.data.scrappedByThisUser);
      } catch (err) {
        toast.error('북마크에 실패하였습니다. 다시 시도해주세요!');
      }
    } else {
      checkIsLogin();
    }
  };

  return { handleClickBookmark };
};
