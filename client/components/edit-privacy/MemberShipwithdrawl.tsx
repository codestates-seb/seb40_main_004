import { isLoginAtom } from '@atoms/loginAtom';
import { dataHeaderAtom } from '@atoms/userAtom';
import { client } from '@libs/client';
import { useRouter } from 'next/router';
import React, { useState } from 'react';
import { confirmAlert } from 'react-confirm-alert';
import { toast } from 'react-toastify';
import { useSetRecoilState } from 'recoil';
import CancelButton from './CancelButton';
import WithDrawalButton from './WithDrawalButton';

const MemberShipWithDrawal = () => {
  const setDataHeader = useSetRecoilState(dataHeaderAtom);
  const [password, setPassword] = useState('');
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const router = useRouter();
  const onSubmitMembershipWithdrawal = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();
    confirmAlert({
      message: '회원 탈퇴하시겠습니까?',
      buttons: [
        {
          label: '예',
          onClick: async () => {
            try {
              await client.delete('/api/auth', {
                data: {
                  password,
                },
              });
              toast.success('회원 탈퇴 완료되었습니다');
              localStorage.clear();
              setDataHeader(null);
              setIsLogin(false);
              router.push('/');
            } catch (error) {
              toast.error(`에러 발생 : ${error}`);
            }
          },
        },
        {
          label: '아니오',
          onClick: () => {
            return;
          },
        },
      ],
    });
  };

  const profileClassName =
    'w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray';

  return (
    <>
      <div className="mb-16">
        <span className="text-3xl font-bold">회원 탈퇴</span>
      </div>
      <form onSubmit={onSubmitMembershipWithdrawal}>
        <label htmlFor="password">비밀번호 입력</label>
        <input
          id="password"
          type="password"
          className={profileClassName}
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
        <div className="flex gap-8">
          <WithDrawalButton />
          <CancelButton />
        </div>
      </form>
    </>
  );
};

export default MemberShipWithDrawal;
