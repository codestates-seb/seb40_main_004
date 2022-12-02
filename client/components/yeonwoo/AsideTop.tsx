/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import { faBloggerB, faGithub } from '@fortawesome/free-brands-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import Image from 'next/image';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import { isLoginAtom, userDashboardAtom } from '../../atomsYW';

export const AsideTop = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const [isEdit, setIsEdit] = useState(false);
  const userDashboard = useRecoilValue(userDashboardAtom);
  const [userId, setUserId] = useState<string | null>('');
  useEffect(() => {
    const id = localStorage.getItem('userId');
    setUserId(id);
  }, []);

  const [editNickname, setEditNickname] = useState('');
  const [editInfoMessage, setEditInfoMessage] = useState('');
  const [editGithub, setEditGithub] = useState('');
  const [editBlog, setEditBlog] = useState('');
  const onClickEdit = () => {
    setIsEdit(true);
    setEditNickname(userDashboard.nickname);
    setEditInfoMessage(userDashboard.infoMessage ?? '');
    setEditGithub(userDashboard.github ?? '');
    setEditBlog(userDashboard.blog ?? '');
  };
  const router = useRouter();
  const onClickCheer = () => {
    isLogin ? router.push('/review') : alert('로그인이 필요합니다.');
  };
  const onSubmitForm = () => {
    axios.patch(
      '/api/users/profiles',
      {
        nickname: editNickname,
        infoMessage: editInfoMessage,
        github: editGithub,
        blog: editBlog,
        jobType: 'DEVELOPER',
      },
      {
        headers: {
          Authorization: localStorage.getItem('accessToken'),
        },
      },
    );
  };
  const changeGradeImoji = (grade: string) => {
    switch (grade) {
      case 'CANDLE':
        return '🕯';
      case 'MATCH':
        return '🔥';
      case 'BONFIRE':
        return '🎇';
      case 'MORAKMORAK':
        return '♨️';
    }
  };
  return (
    <>
      {isEdit ? (
        <>
          <div className="rounded-full w-[238px] h-[238px] overflow-hidden">
            <Image
              src={userDashboard.avatar?.remotePath ?? '/favicon.ico'}
              width="238px"
              height="238px"
            />
          </div>
          <form className="mt-2" onSubmit={onSubmitForm}>
            <div className="flex justify-between items-baseline">
              <input
                className="text-3xl font-bold border border-main-gray rounded-full pl-4 w-[238px]"
                value={editNickname}
                onChange={(e) => setEditNickname(e.target.value)}
                placeholder="닉네임"
              />
            </div>
            <div className="-mt-1">
              <span className="text-sm">{userDashboard.email}</span>
            </div>
            <div className="my-2">
              <input
                className="text-xl font-medium border border-main-gray rounded-full pl-4 w-[238px]"
                value={editInfoMessage}
                onChange={(e) => setEditInfoMessage(e.target.value)}
                placeholder="인포 메세지"
              />
            </div>
            <div className="flex justify-between items-start">
              <div>
                <span className="text-xl text-main-orange font-semibold">
                  {`${userDashboard.point} 모락`}
                </span>
              </div>
              <div className="flex gap-4 text-xl">
                <span className="text-2xl">
                  {changeGradeImoji(userDashboard.grade)}
                </span>
                <span>{`# ${userDashboard.rank}`}</span>
              </div>
            </div>
            <div className="mt-2 mb-4">
              <input
                className="border border-main-gray rounded-full pl-4 w-[238px] mb-2"
                value={editGithub}
                onChange={(e) => setEditGithub(e.target.value)}
                placeholder="깃허브 주소"
              />
              <input
                className="border border-main-gray rounded-full pl-4 w-[238px]"
                value={editBlog}
                onChange={(e) => setEditBlog(e.target.value)}
                placeholder="블로그 주소"
              />
            </div>
            <div className="w-[238px] flex justify-between">
              <button className="bg-main-yellow rounded-full py-[6px] w-32">
                저장
              </button>
              <button
                className="bg-main-gray rounded-full py-[6px] w-32"
                onClick={() => setIsEdit(false)}
              >
                취소
              </button>
            </div>
          </form>
        </>
      ) : (
        <>
          <div className="rounded-full w-[238px] h-[238px] overflow-hidden">
            <Image
              src={userDashboard.avatar?.remotePath ?? '/favicon.ico'}
              width="238px"
              height="238px"
            />
          </div>
          <div className="mt-2">
            <div className="flex justify-between items-baseline w-[238px]">
              <div className="w-[168px]">
                {userDashboard.nickname.length > 6 ? (
                  <>
                    <div className="w-[168px] text-3xl font-bold">
                      {userDashboard.nickname.slice(0, 6)}
                    </div>
                    <div className="w-[168px] text-3xl font-bold">
                      {userDashboard.nickname.slice(6)}
                    </div>
                  </>
                ) : (
                  <span className="w-[168px] text-3xl font-bold">
                    {userDashboard.nickname}
                  </span>
                )}
              </div>
              <div className="w-[80px] flex justify-end">
                <span className="text-sm">
                  {userDashboard.jobType === 'DEVELOPER'
                    ? '현업 개발자'
                    : userDashboard.jobType === 'JOB_SEEKER'
                    ? '취준 개발자'
                    : userDashboard.jobType === 'NON_NORMAL'
                    ? '비개발자'
                    : userDashboard.jobType === 'PM'
                    ? '매니저'
                    : '디자이너'}
                </span>
              </div>
            </div>
            <div className="-mt-1">
              <span className="text-sm">{userDashboard.email}</span>
            </div>
            <div className="my-2 max-w-[238px] flex-col">
              <span className="text-xl font-medium max-w-[238px] overflow-scro">
                {userDashboard.infoMessage ?? ''}
              </span>
            </div>
            <div className="flex justify-between items-baseline">
              <div>
                <span className="text-xl text-main-orange font-semibold">
                  {`${userDashboard.point} 모락`}
                </span>
              </div>
              <div className="flex gap-4 text-xl">
                <span className="text-2xl">
                  {changeGradeImoji(userDashboard.grade)}
                </span>
                <span>{`# ${userDashboard.rank}`}</span>
              </div>
            </div>
            <div className="mt-2 mb-4 flex gap-8">
              {userDashboard.github && (
                <a target="_blank" href={userDashboard.github} rel="noopener">
                  <FontAwesomeIcon icon={faGithub} size="2xl" />
                </a>
              )}
              {userDashboard.blog && (
                <a target="_blank" href={userDashboard.blog} rel="noopener">
                  <FontAwesomeIcon icon={faBloggerB} size="2xl" />
                </a>
              )}
            </div>
            <div>
              <button
                className="bg-main-yellow py-[6px] rounded-full w-[238px]"
                onClick={
                  userId === userDashboard.userId + ''
                    ? onClickEdit
                    : onClickCheer
                }
              >
                {userId === userDashboard.userId + ''
                  ? '프로필 수정'
                  : '응원 하기'}
              </button>
            </div>
          </div>
        </>
      )}
    </>
  );
};
