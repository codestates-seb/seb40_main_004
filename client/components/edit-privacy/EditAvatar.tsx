import Image from 'next/image';
import { ChangeEvent, useEffect, useState } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';

import { renderingAtom } from '@atoms/renderingAtom';
import { dataHeaderAtom } from '@atoms/userAtom';

import { client } from '@libs/client';
import { uploadImg } from '@libs/uploadS3';

export const EditAvatar = () => {
  const setRenderingHeader = useSetRecoilState(renderingAtom);
  const dataHeader = useRecoilValue(dataHeaderAtom);
  const [isClicked, setIsClicked] = useState(false);
  const [isValid, setIsValid] = useState(true);
  const [avatarPath, setAvatarPath] = useState('/favicon.ico');
  const onChangeFile = async (event: ChangeEvent<HTMLInputElement>) => {
    try {
      const res = await client.get('/api/users/profiles/avatars');
      const {
        target: { files },
      } = event;
      const file = files && files[0];
      await uploadImg(res.data.preSignedUrl, file);
      {
        /*setTimeout(() => {
        setAvatarPath(res.data.preSignedUrl);
      }, 2000);*/
      }
      setIsClicked(false);
      alert('프로필이 정상적으로 변경되었습니다!');
      setRenderingHeader((prev) => !prev);
    } catch (error) {
      alert('오류가 발생했습니다. 다시 시도해주세요!');
    }
  };
  const onClickDelete = async () => {
    try {
      await client.delete('/api/users/profiles/avatars');
      localStorage.removeItem('avatarPath');
      setIsClicked(false);
      alert('프로필이 정상적으로 삭제되었습니다!');
      setRenderingHeader((prev) => !prev);
      setAvatarPath('/favicon.ico');
    } catch (error) {
      alert('오류가 발생했습니다. 다시 시도해주세요!');
      console.error(error);
    }
  };
  useEffect(() => {
    dataHeader?.avatar
      ? dataHeader?.avatar.remotePath
        ? setAvatarPath(dataHeader?.avatar.remotePath)
        : '/favicon.ico'
      : '/favicon.ico';
  }, [dataHeader]);
  return (
    <>
      <div className="relative group">
        <div className="w-[238px] h-[238px] rounded-full overflow-hidden">
          {isValid ? (
            <Image
              src={avatarPath ?? '/favicon.cio'}
              width="238px"
              height="238px"
              onError={() => setIsValid(false)}
            />
          ) : (
            <Image src="/favicon.ico" width="238px" height="238px" />
          )}
        </div>
        <button
          className="py-[6px] px-2 absolute left-4 bottom-4 bg-background-gray rounded-full"
          onClick={() => setIsClicked((prev) => !prev)}
        >
          ✏️ 편집
        </button>
      </div>
      {isClicked ? (
        <div className="relative -left-8 bottom-8">
          <ul className="border border-solid border-black border-opacity-10 border-spacing-1 right-0 w-[200px] rounded-xl absolute top-8 bg-background-gray z-20">
            <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer mt-2 py-1 px-4 rounded-xl text-[15px]">
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
              className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer py-1 mb-2 px-4 rounded-xl text-[15px]"
              onClick={onClickDelete}
            >
              <button className="ml-2">삭제</button>
            </li>
          </ul>
        </div>
      ) : null}
    </>
  );
};
