import { useRouter } from 'next/router';
import { FormEvent, useEffect, useState } from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useSetRecoilState } from 'recoil';

import { isLoginAtom } from '@atoms/loginAtom';
import { renderingAtom } from '@atoms/renderingAtom';
import { dataHeaderAtom } from '@atoms/userAtom';

import { client } from '@libs/client';
import { inspectNicknameDuplication } from '@libs/inspectNicknameDuplication';

import { UserDashboard } from '@type/dashboard';
import { toast } from 'react-toastify';
import { confirmAlert } from 'react-confirm-alert';
import CancelButton from './CancelButton';
import ProfileEditTitle from './ProfileEditTitle';
import SaveButton from './SaveButton';

import ChangeButton from './ChangeButton';
import WithDrawalButton from './WithDrawalButton';
import { jobOptions } from '@libs/jobOptions';
import SelectJobOption from './SelectJobOption';
import OriginalPasswordInput from './OriginalPasswordInput';
import NewPasswordInput from './NewPasswordInput';

type UserCredentialsUpdate = {
  originalPassword: string;
  newPassword: string;
  newPasswordCheck: string;
  infoMessage: string;
  github: string;
  jobType: string;
  blog: string;
};

type ErrorProps = {
  errorType: boolean;
  message: string | undefined;
};

export const EditProfileComponent = () => {
  const setDataHeader = useSetRecoilState(dataHeaderAtom);
  const setRenderingHeader = useSetRecoilState(renderingAtom);
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const [pathname, setPathname] = useState('');
  const [userId, setUserId] = useState('');
  const [accessToken, setAccessToken] = useState('');
  const [userData, setUserData] = useState<UserDashboard>();
  const [nickname, setNickname] = useState('');
  const [infoMessage, setInfoMessage] = useState('');
  const [github, setGithub] = useState('');
  const [blog, setBlog] = useState('');
  const [jobType, setJobType] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<UserCredentialsUpdate>();

  const onValid: SubmitHandler<UserCredentialsUpdate> = async ({
    originalPassword,
    newPassword,
    newPasswordCheck,
  }) => {
    newPassword !== newPasswordCheck
      ? setError(
          'newPasswordCheck',
          { message: '비밀번호가 맞지 않습니다.' },
          { shouldFocus: true },
        )
      : confirmAlert({
          message: '비밀번호를 변경 하시겠습니까?',
          buttons: [
            {
              label: '예',
              onClick: async () => {
                try {
                  await client.patch('/api/auth/password', {
                    originalPassword,
                    newPassword,
                  });
                  toast.success('비밀번호가 정상적으로 변경 되었습니다');
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
  const getPathname = () => {
    setPathname(router.pathname);
  };
  const getUserId = () => {
    if (typeof window !== 'undefined') {
      const data = localStorage.getItem('userId');
      data ? setUserId(data) : null;
    }
  };
  const getAccessToken = () => {
    if (typeof window !== 'undefined') {
      const data = localStorage.getItem('accessToken');
      data ? setAccessToken(data) : null;
    }
  };
  const getUserData = async () => {
    if (userId) {
      const res = await client.get(`/api/users/${userId}/dashboard`);
      setUserData(res.data);
    }
  };

  useEffect(() => {
    getPathname();
    getUserId();
    getAccessToken();
  }, []);

  useEffect(() => {
    getUserData();
  }, [userId]);

  useEffect(() => {
    userData?.nickname && setNickname(userData.nickname);
    userData?.infoMessage && setInfoMessage(userData.infoMessage);
    userData?.github && setGithub(userData.github);
    userData?.blog && setBlog(userData.blog);
    userData?.jobType && setJobType(userData.jobType);
  }, [userData]);

  const onSubmitEditProfile = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const reg = new RegExp('^(?=.*[a-z0-9가-힣])[a-z0-9가-힣].{0,6}$');

    !reg.test(nickname)
      ? alert('닉네임은 최소 1글자, 최대 7글자, 자음, 모음 불가입니다')
      : confirmAlert({
          message: '프로필을 저장 하시겠습니까?',
          buttons: [
            {
              label: 'YES',
              onClick: async () => {
                try {
                  await client.patch('/api/users/profiles', {
                    nickname,
                    infoMessage,
                    github,
                    blog,
                    jobType,
                  });
                  localStorage.setItem('nickname', nickname);
                  setRenderingHeader((prev) => !prev);
                  router.push('/');
                } catch (error) {
                  toast.error(`에러 발생 : ${error}`);
                }
              },
            },
            {
              label: 'NO',
              onClick: () => {
                return;
              },
            },
          ],
        });
  };
  const onSubmitMembershipWithdrawal = async (
    event: FormEvent<HTMLFormElement>,
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

  const onBlurNickname = () => {
    inspectNicknameDuplication(userData?.nickname ?? '', nickname);
  };

  const ErrorMessage = ({ errorType, message }: ErrorProps) => {
    return errorType ? (
      <p className="font-bold text-red-500">{message}</p>
    ) : null;
  };

  const profileClassName =
    'w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray';

  return (
    <>
      {pathname === '/edit-profile' ? (
        <>
          <ProfileEditTitle />
          <form onSubmit={onSubmitEditProfile}>
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              type="text"
              placeholder="닉네임을 입력해주세요"
              className={profileClassName}
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              onBlur={onBlurNickname}
            />
            <label htmlFor="message">메세지</label>
            <input
              id="message"
              type="text"
              placeholder="메세지를 입력해주세요"
              className={profileClassName}
              {...register('infoMessage', { required: true })}
            />
            <label htmlFor="github">깃허브 주소</label>
            <input
              id="github"
              type="text"
              placeholder="깃허브 주소를 입력해주세요"
              className={profileClassName}
              {...register('github', { required: true })}
            />
            <label htmlFor="blog">블로그 주소</label>
            <input
              id="blog"
              type="text"
              placeholder="블로그 주소를 입력해주세요"
              className={profileClassName}
              {...register('blog', { required: true })}
            />
            <label htmlFor="userState">직업 현황</label>
            <select
              id="userState"
              className="w-full rounded-full h-11 px-4 mt-2 mb-32 border border-main-gray"
              {...register('jobType', { required: true })}
            >
              <SelectJobOption options={jobOptions} />
            </select>
            <div className="flex gap-8">
              <SaveButton />
              <CancelButton />
            </div>
          </form>
        </>
      ) : pathname === '/edit-password' ? (
        <>
          <div className="mb-16">
            <span className="text-3xl font-bold">비밀번호 변경</span>
          </div>
          <form onSubmit={handleSubmit(onValid)}>
            <div className="mt-2 mb-10">
              <label htmlFor="original-password">기존 비밀번호</label>
              <OriginalPasswordInput />
              <ErrorMessage
                errorType={errors.originalPassword?.type === 'pattern'}
                message="비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다."
              />
            </div>
            <div className="mt-2 mb-10">
              <label htmlFor="new-password">신규 비밀번호</label>
              <NewPasswordInput />
              <ErrorMessage
                errorType={errors.newPassword?.type === 'pattern'}
                message="비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다."
              />
            </div>
            <div className="mt-2 mb-10">
              <label htmlFor="new-password-check">신규 비밀번호 확인</label>
              <ErrorMessage
                errorType={errors.newPasswordCheck?.type === 'pattern'}
                message={errors.newPasswordCheck?.message}
              />
            </div>
            <div className="flex gap-8">
              <ChangeButton />
              <CancelButton />
            </div>
          </form>
        </>
      ) : pathname === '/membership-withdrawal' ? (
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
      ) : (
        <h1>올바르지 않은 접근입니다</h1>
      )}
    </>
  );
};
