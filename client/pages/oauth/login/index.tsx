import { useRouter } from 'next/router';
import { useEffect } from 'react';
import axios from 'axios';
import { useSetRecoilState } from 'recoil';
import { isLoginAtom } from '../../../atomsYW';
import jwt_decode from 'jwt-decode';
import { DecodedProps } from '../../../libs/interfaces';
import { Loader } from '../../../components/common/Loader';

const OAuth2Login = () => {
  // uri 에 담겨오는 리프레시 토큰 추출
  const router = useRouter();
  const { RefreshToken } = router.query;

  const setIsLogin = useSetRecoilState(isLoginAtom);

  const handler = async (r: string | string[] | undefined) => {
    // 받아온 리프레시 토큰으로 액세스 토큰 등을 받아오는 put 요청 실행
    await axios
      .put(
        '/api/auth/token',
        {},
        {
          headers: {
            RefreshToken: r,
          },
        },
      )
      .then((res) => {
        // 해당 요청에 성공했을 경우, 데이터 추출하여 로컬스토리지 저장
        console.log(res);
        const { accessToken, refreshToken, avatarPath } = res.data;
        const decoded: DecodedProps = jwt_decode(accessToken);
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('avatarPath', avatarPath);
        localStorage.setItem('email', decoded.sub);
        localStorage.setItem('userId', String(decoded.id));
        localStorage.setItem('nickname', decoded.nickname);
        // 로그인 상태를 true 로 변경한 후 메인페이지로 이동
        setIsLogin(true);
        router.replace('/');

        console.log(decoded);
      })
      .catch((err) => console.log(err.response));
  };

  // uri 로 리프레시 토큰을 받아오게 되면 핸들러 함수 실행
  useEffect(() => {
    handler(RefreshToken);
  }, [RefreshToken]);

  return (
    <div className="flex h-screen justify-center items-center">
      <Loader />
    </div>
  );
};

export default OAuth2Login;
