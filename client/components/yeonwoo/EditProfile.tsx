/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-20
 */

import axios from 'axios';
import { useRouter } from 'next/router';
import { FormEvent, useEffect, useState } from 'react';
import { userDashboard } from '../../interfaces';

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
      const res = await axios.get(`/api/users/${userId}/dashboard`);
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
  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await axios.patch(
      '/api/users/profiles',
      {
        nickname,
        infoMessage,
        github,
        blog,
        jobType,
      },
      {
        headers: {
          Authorization: accessToken,
        },
      },
    );
    localStorage.setItem('nickname', nickname);
    router.push('/');
  };
  return (
    <>
      {pathname === '/edit-profile' ? (
        <>
          <div className="mb-16">
            <span className="text-3xl font-bold">프로필 수정</span>
          </div>
          <form onSubmit={onSubmit}>
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
              <button className="w-full py-[6px] rounded-full bg-main-gray">
                취소
              </button>
            </div>
          </form>
        </>
      ) : pathname === '/edit-password' ? (
        <>
          <div className="mb-16">
            <span className="text-3xl font-bold">프로필 수정</span>
          </div>
          <form>
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
            />
            <label htmlFor="message">메세지</label>
            <input
              id="message"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
            />
            <label htmlFor="github">깃허브 주소</label>
            <input
              id="github"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
            />
            <label htmlFor="blog">블로그 주소</label>
            <input
              id="blog"
              type="text"
              className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
            />
            <label htmlFor="userState">취준/현업/무관</label>
            <select
              id="userState"
              className="w-full rounded-full h-11 px-4 mt-2 mb-32 border border-main-gray"
            >
              <option value="student">개발자 취준생</option>
              <option value="developer">현업 개발자</option>
              <option value="citizen">개발에 관심있는 일반인</option>
            </select>
            <div className="flex gap-8">
              <button className="w-full py-[6px] rounded-full bg-main-yellow">
                저장
              </button>
              <button className="w-full py-[6px] rounded-full bg-main-gray">
                취소
              </button>
            </div>
          </form>
        </>
      ) : (
        <h1>올바르지 않은 접근입니다</h1>
      )}
    </>
  );
};
