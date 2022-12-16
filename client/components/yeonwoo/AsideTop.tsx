/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-12-04
 */

import { faBloggerB, faGithub } from '@fortawesome/free-brands-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import { useRouter } from 'next/router';
import { ChangeEvent, FormEvent, useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { isLoginAtom, renderingAtom, userDashboardAtom } from '../../atomsYW';
import { changeGradeEmoji } from '../../libs/changeGradeEmoji';
import { client } from '../../libs/client';
import { inspectNicknameDuplication } from '../../libs/inspectNicknameDuplication';
import { uploadImg } from '../../libs/uploadS3';

export const AsideTop = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const userDashboard = useRecoilValue(userDashboardAtom);
  const setRenderingHeader = useSetRecoilState(renderingAtom);
  const [userId, setUserId] = useState<string | null>('');
  const [isEdit, setIsEdit] = useState(false);
  const [isClicked, setIsClicked] = useState(false);
  useEffect(() => {
    const id = localStorage.getItem('userId');
    setUserId(id);
  }, []);

  const [editNickname, setEditNickname] = useState('');
  const [editInfoMessage, setEditInfoMessage] = useState('');
  const [editGithub, setEditGithub] = useState('');
  const [editBlog, setEditBlog] = useState('');
  const [editJobType, setEditJobType] = useState('');
  const onClickEdit = () => {
    setIsEdit(true);
    setEditNickname(userDashboard.nickname);
    setEditInfoMessage(userDashboard.infoMessage ?? '');
    setEditGithub(userDashboard.github ?? '');
    setEditBlog(userDashboard.blog ?? '');
    setEditJobType(userDashboard.jobType ?? '');
  };
  const router = useRouter();
  const onClickCheer = () => {
    isLogin ? router.push('/review') : toast.error('로그인이 필요합니다.');
  };
  const onSubmitForm = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const reg = new RegExp('^(?=.*[a-z0-9가-힣])[a-z0-9가-힣].{0,6}$');
    if (!reg.test(editNickname)) {
      toast.error('닉네임은 최소 1글자, 최대 7글자, 자음, 모음 불가입니다');
    } else {
      try {
        await client.patch('/api/users/profiles', {
          nickname: editNickname,
          infoMessage: editInfoMessage,
          github: editGithub,
          blog: editBlog,
          jobType: editJobType,
        });
        setIsEdit(false);
        setIsClicked(false);
        setRenderingHeader((prev) => !prev);
      } catch (error) {
        toast.error('에러가 발생했습니다 다시 시도해주세요');
        console.error(error);
      }
    }
  };

  const onChangeFile = async (event: ChangeEvent<HTMLInputElement>) => {
    try {
      const res = await client.get('/api/users/profiles/avatars');
      const {
        target: { files },
      } = event;
      const file = files && files[0];
      await uploadImg(res.data.preSignedUrl, file);
      setIsClicked(false);
      toast.error('프로필이 정상적으로 변경되었습니다!');
      setRenderingHeader((prev) => !prev);
    } catch (error) {
      toast.error('오류가 발생했습니다. 다시 시도해주세요!');
    }
  };

  const onClickDelete = async () => {
    try {
      await client.delete('/api/users/profiles/avatars');
      localStorage.removeItem('avatarPath');
      setIsClicked(false);
      toast.error('프로필이 정상적으로 삭제되었습니다!');
      setRenderingHeader((prev) => !prev);
    } catch (error) {
      toast.error('오류가 발생했습니다. 다시 시도해주세요!');
      console.error(error);
    }
  };

  const onBlurNickname = () => {
    inspectNicknameDuplication(userDashboard.nickname, editNickname);
  };

  return (
    <>
      {isEdit ? (
        <>
          <div className="relative">
            <div className="rounded-full w-[238px] h-[238px] overflow-hidden">
              <Image
                src={userDashboard.avatar?.remotePath ?? '/favicon.ico'}
                width="238px"
                height="238px"
              />
            </div>
            <button
              className="py-[6px] px-2 absolute left-4 bottom-4 bg-background-gray rounded-full text-sm border border-[#D9D9D9]"
              onClick={() => setIsClicked((prev) => !prev)}
            >
              ✏️ 편집
            </button>
            {isClicked ? (
              <div className="relative -left-8 bottom-8">
                <ul className="border border-solid border-black border-opacity-10 border-spacing-1 right-0 w-[200px] rounded-xl absolute top-8 bg-background-gray z-20">
                  <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer mt-2 py-1 px-4 rounded-xl text-sm">
                    <label
                      htmlFor="attach-file"
                      className="ml-2 hover:cursor-pointer"
                    >
                      업데이트
                    </label>
                    <input
                      id="attach-file"
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={onChangeFile}
                    />
                  </li>
                  <li
                    className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer py-1 mb-2 px-4 rounded-xl text-sm"
                    onClick={onClickDelete}
                  >
                    <button className="ml-2">삭제</button>
                  </li>
                </ul>
              </div>
            ) : null}
          </div>
          <form
            className={`${isClicked ? 'mt-24' : 'mt-2'}`}
            onSubmit={onSubmitForm}
          >
            <div className="flex w-[238px]">
              <div className="flex justify-between items-center">
                <input
                  className="text-xl font-bold border border-main-gray rounded-full pl-4 w-[148px]"
                  placeholder="닉네임"
                  value={editNickname}
                  onChange={(e) => setEditNickname(e.target.value)}
                  onBlur={onBlurNickname}
                />
              </div>
              <select
                id="userState"
                className="w-full text-xs rounded-full pl-2 py-[0.37rem] my-2 border border-main-gray"
                value={editJobType}
                onChange={(e) => setEditJobType(e.target.value)}
              >
                <option value="JOB_SEEKER">취준 개발자</option>
                <option value="DEVELOPER">현업 개발자</option>
                <option value="DESIGNER">디자이너</option>
                <option value="PM">프로덕트 매니저</option>
                <option value="NON_NORMAL">비개발 직군</option>
                <option value="DEFAULT">🤔🧑‍💻🤗</option>
              </select>
            </div>
            <div className="-mt-1">
              <span className="text-xs opacity-80 pl-4">
                {userDashboard.email}
              </span>
            </div>
            <div className="my-2">
              <input
                className="text-lg font-medium border border-main-gray rounded-full pl-4 w-[238px]"
                value={editInfoMessage}
                onChange={(e) => setEditInfoMessage(e.target.value)}
                placeholder="메세지"
              />
            </div>
            <div className="flex justify-between items-start">
              <div>
                <span className="text-main-orange font-semibold">
                  {`${userDashboard.point} 모락`}
                </span>
              </div>
              <div className="flex gap-4 items-baseline">
                <span className="text-lg">
                  {changeGradeEmoji(userDashboard.grade)}
                </span>
                <span>{`# ${userDashboard.rank}`}</span>
              </div>
            </div>
            <div className="mt-2 mb-4">
              <input
                className="border border-main-gray rounded-full pl-4 w-[238px] mb-2 text-sm"
                value={editGithub}
                onChange={(e) => setEditGithub(e.target.value)}
                placeholder="깃허브 주소"
              />
              <input
                className="border border-main-gray rounded-full pl-4 w-[238px] text-sm"
                value={editBlog}
                onChange={(e) => setEditBlog(e.target.value)}
                placeholder="블로그 주소"
              />
            </div>
            <div className="w-[238px] flex justify-between">
              <button className="bg-main-yellow rounded-full py-[6px] w-32 text-sm">
                저장
              </button>
              <button
                className="bg-main-gray rounded-full py-[6px] w-32 text-sm"
                onClick={() => {
                  setIsEdit(false);
                  setIsClicked(false);
                }}
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
                <span className="w-[168px] text-xl font-bold">
                  {userDashboard.nickname}
                </span>
              </div>
              <div className="w-[80px] flex justify-end">
                <span className="text-xs">
                  {userDashboard.jobType === 'DEVELOPER'
                    ? '현업 개발자'
                    : userDashboard.jobType === 'JOB_SEEKER'
                    ? '취준 개발자'
                    : userDashboard.jobType === 'NON_NORMAL'
                    ? '비개발자'
                    : userDashboard.jobType === 'PM'
                    ? '매니저'
                    : userDashboard.jobType === 'DESIGNER'
                    ? '디자이너'
                    : userDashboard.jobType === 'DEFAULT'
                    ? '🤔🧑‍💻🤗'
                    : '?'}
                </span>
              </div>
            </div>
            <div className="-mt-1">
              <span className="text-xs opacity-80">{userDashboard.email}</span>
            </div>
            <div className="my-2 max-w-[238px] flex-col">
              <span className="text-lg font-medium max-w-[238px] overflow-scro">
                {userDashboard.infoMessage ?? ''}
              </span>
            </div>
            <div className="flex justify-between items-baseline">
              <div>
                <span className="text-main-orange font-semibold">
                  {`${userDashboard.point} 모락`}
                </span>
              </div>
              <div className="flex gap-4 items-baseline">
                <span className="text-lg">
                  {changeGradeEmoji(userDashboard.grade)}
                </span>
                <span>{`# ${userDashboard.rank}`}</span>
              </div>
            </div>
            <div className="mt-2 mb-4 flex gap-8">
              {userDashboard.github && (
                <a target="_blank" href={userDashboard.github} rel="noopener">
                  <FontAwesomeIcon icon={faGithub} size="xl" />
                </a>
              )}
              {userDashboard.blog && (
                <a target="_blank" href={userDashboard.blog} rel="noopener">
                  <FontAwesomeIcon icon={faBloggerB} size="xl" />
                </a>
              )}
            </div>
            <div>
              <button
                className="bg-main-yellow py-[6px] rounded-full w-[238px] text-sm"
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
