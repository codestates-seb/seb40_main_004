import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../atomsYW';

export const useCheckClickIsLogin = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const router = useRouter();
  const checkIsLogin = () => {
    if (
      !isLogin &&
      confirm('로그인이 필요한 서비스 입니다. 바로 로그인 하시겠어요?')
    ) {
      router.replace('/login');
    }
  };

  return checkIsLogin;
};
