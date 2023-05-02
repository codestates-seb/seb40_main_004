import { confirmAlert } from 'react-confirm-alert';
import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';

import { isLoginAtom } from '@atoms/loginAtom';

export const useCheckClickIsLogin = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const router = useRouter();
  const checkIsLogin = () => {
    if (!isLogin) {
      confirmAlert({
        message: '로그인이 필요한 서비스 입니다. 바로 로그인 하시겠어요?',
        buttons: [
          {
            label: 'YES',
            onClick: () => router.push('/login'),
          },
          {
            label: 'NO',
            onClick: () => router.back(),
          },
        ],
      });
    }
  };

  return checkIsLogin;
};
