/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-12-02
 */

import Link from 'next/link';
import { useRouter } from 'next/router';
import { FormEvent, useEffect, useState } from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import { userDashboard } from '../../interfaces';
import { client } from '../../libs/client';

interface IChangePassword {
  originalPassword: string;
  newPassword: string;
  newPasswordCheck: string;
}

export const EditProfileComponent = () => {
  const [pathname, setPathname] = useState('');
  const [userId, setUserId] = useState('');
  const [accessToken, setAccessToken] = useState('');
  const [userData, setUserData] = useState<userDashboard>();
  const [nickname, setNickname] = useState('');
  const [infoMessage, setInfoMessage] = useState('');
  const [github, setGithub] = useState('');
  const [blog, setBlog] = useState('');
  const [jobType, setJobType] = useState('');
  const router = useRouter();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<IChangePassword>();
  const onValid: SubmitHandler<IChangePassword> = async ({
    originalPassword,
    newPassword,
    newPasswordCheck,
  }) => {
    if (newPassword !== newPasswordCheck) {
      setError(
        'newPasswordCheck',
        { message: '비밀번호가 맞지 않습니다.' },
        { shouldFocus: true },
      );
    } else {
      try {
        await client.patch('/api/auth/password', {
          originalPassword,
          newPassword,
        });
        alert('비밀번호가 정상적으로 변경 되었습니다');
        router.push('/');
      } catch (error) {
        alert(`에러 발생 : ${error}`);
      }
    }
  };
  const getPathname = () => {
    setPathname(router.pathname);
  };
  const getUserId = () => {
    if (typeof window !== 'undefined') {
      const data = localStorage.getItem('userId');
      data && setUserId(data);
    }
  };
  const getAccessToken = () => {
    if (typeof window !== 'undefined') {
      const data = localStorage.getItem('accessToken');
      data && setAccessToken(data);
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
    try {
      await client.patch('/api/users/profiles', {
        nickname,
        infoMessage,
        github,
        blog,
        jobType,
      });
      localStorage.setItem('nickname', nickname);
      router.push('/');
    } catch (error) {
      alert(`에러 발생 : ${error}`);
    }
  };
  return (
    <>
      {pathname === '/edit-profile' ? (
        <>
          <div className="mb-16">
            <span className="text-3xl font-bold">프로필 수정</span>
          </div>
          <form onSubmit={onSubmitEditProfile}>
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
            />
            <label htmlFor="message">메세지</label>
            <input
              id="message"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
              value={infoMessage}
              onChange={(e) => setInfoMessage(e.target.value)}
            />
            <label htmlFor="github">깃허브 주소</label>
            <input
              id="github"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
              value={github}
              onChange={(e) => setGithub(e.target.value)}
            />
            <label htmlFor="blog">블로그 주소</label>
            <input
              id="blog"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
              value={blog}
              onChange={(e) => setBlog(e.target.value)}
            />
            <label htmlFor="userState">취준/현업/무관</label>
            <select
              id="userState"
              className="w-full rounded-full h-11 px-4 mt-2 mb-32 border border-main-gray"
              value={jobType}
              onChange={(e) => setJobType(e.target.value)}
            >
              <option value="JOB_SEEKER">개발자 취준생</option>
              <option value="DEVELOPER">현업 개발자</option>
              <option value="DESIGNER">디자이너</option>
              <option value="PM">프로덕트 매니저</option>
              <option value="NON_NORMAL">비개발 직군</option>
            </select>
            <div className="flex gap-8">
              <button className="w-full py-[6px] rounded-full bg-main-yellow">
                저장
              </button>
              <Link href="/">
                <div className="w-full py-[6px] rounded-full bg-main-gray hover:cursor-pointer flex justify-center">
                  <span>취소</span>
                </div>
              </Link>
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
              <input
                id="original-password"
                type="password"
                className="w-full rounded-full h-11 px-4 border border-main-gray"
                placeholder="기존 비밀번호 입력"
                {...register('originalPassword', {
                  required: '기존 비밀번호는 필수 입력 사항입니다',
                  pattern: {
                    value:
                      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
                    message:
                      '비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다',
                  },
                })}
              />
              {errors.originalPassword && (
                <p className="font-semibold text-red-500 text-sm text-center">
                  {errors.originalPassword.message}
                </p>
              )}
            </div>
            <div className="mt-2 mb-10">
              <label htmlFor="new-password">신규 비밀번호</label>
              <input
                id="new-password"
                type="password"
                className="w-full rounded-full h-11 px-4 border border-main-gray"
                placeholder="신규 비밀번호 입력"
                {...register('newPassword', {
                  required: '신규 비밀번호는 필수 입력 사항입니다',
                  pattern: {
                    value:
                      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
                    message:
                      '비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다',
                  },
                })}
              />
              {errors.newPassword && (
                <p className="font-semibold text-red-500 text-sm text-center">
                  {errors.newPassword.message}
                </p>
              )}
            </div>
            <div className="mt-2 mb-10">
              <label htmlFor="new-password-check">신규 비밀번호 확인</label>
              <input
                id="new-password-check"
                type="password"
                className="w-full rounded-full h-11 px-4 border border-main-gray"
                placeholder="신규 비밀번호 확인"
                {...register('newPasswordCheck', {
                  required: '신규 비밀번호 확인은 필수 입력 사항입니다',
                  pattern: {
                    value:
                      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
                    message:
                      '비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다',
                  },
                })}
              />
              {errors.newPasswordCheck && (
                <p className="font-semibold text-red-500 text-sm text-center">
                  {errors.newPasswordCheck.message}
                </p>
              )}
            </div>
            <div className="flex gap-8">
              <button className="w-full py-[6px] rounded-full bg-main-yellow">
                변경
              </button>
              <Link href="/">
                <div className="w-full py-[6px] rounded-full bg-main-gray hover:cursor-pointer flex justify-center">
                  <span>취소</span>
                </div>
              </Link>
            </div>
          </form>
        </>
      ) : (
        <h1>올바르지 않은 접근입니다</h1>
      )}
    </>
  );
};
