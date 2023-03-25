import { BtnUser } from './BtnUser';
import { BtnLogin } from './BtnLogin';
import { Logo } from './Logo';
import { Nav } from './Nav';
import { SearchBar } from './SearchBar';
<<<<<<< HEAD:client/components/common/Header.tsx
import BtnDropdown from '../common';
=======
import { BtnDropdown } from './BtnDropdown';
>>>>>>> ce718f293ca7535492d6168f43ad435fbdfaf9ff:client/components/common/Header/index.tsx
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import { useEffect } from 'react';
import { client } from '@libs/client';

import { isLoginAtom } from '@atoms/loginAtom';
import { renderingAtom } from '@atoms/renderingAtom';
import { dataHeaderAtom } from '@atoms/userAtom';

import { IDataHeader } from '@type/user';

export const Header = () => {
  const [isLogin, setIsLogin] = useRecoilState(isLoginAtom);
  const rendering = useRecoilValue(renderingAtom);
  const setDataHeader = useSetRecoilState(dataHeaderAtom);
  const getDataHeader = async () => {
    if (isLogin) {
      const res = await client.get<IDataHeader>('/api/users/points');
      setDataHeader(res.data);
      res.data.avatar &&
        res.data.avatar.remotePath &&
        localStorage.setItem('avatarPath', res.data.avatar.remotePath);
      res.data.userInfo.grade &&
        localStorage.setItem('grade', res.data.userInfo.grade);
    }
  };
  useEffect(() => {
    if (typeof window !== 'undefined') {
      if (localStorage.getItem('refreshToken')) {
        setIsLogin(true);
      } else {
        setIsLogin(false);
      }
    } else {
      setIsLogin(false);
    }
    getDataHeader();
  }, [rendering]);
  return (
    <header className="flex items-center justify-between max-w-[1280px] mx-auto mobile:flex mobile:flex-col mobile:space-y-2">
      <div className="flex items-center w-6/12 mobile:flex mobile:flex-col mobile:items-center">
        <Logo />
        <Nav />
      </div>
      <div className="flex items-center w-3/12 mobile:w-72">
        <SearchBar />
      </div>
      <div className="flex items-center w-3/12 justify-end mobile:w-full mobile:flex mobile:justify-center">
        {isLogin ? (
          <div className="flex gap-6 mobile:flex mobile:justify-center">
            <BtnUser />
            <BtnDropdown />
          </div>
        ) : (
          <BtnLogin />
        )}
      </div>
    </header>
  );
};
