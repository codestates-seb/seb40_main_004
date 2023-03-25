import { renderingAtom } from '@atoms/renderingAtom';
import { client } from '@libs/client';
import { inspectNicknameDuplication } from '@libs/inspectNicknameDuplication';
import { jobOptions } from '@libs/jobOptions';
import { UserDashboard } from '@type/dashboard';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { confirmAlert } from 'react-confirm-alert';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { useSetRecoilState } from 'recoil';
import CancelButton from './CancelButton';
import ProfileEditTitle from './ProfileEditTitle';
import SaveButton from './SaveButton';
import SelectJobOption from './SelectJobOption';

const profileClassName =
  'w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray';

const EditNickName = ({
  userData,
}: {
  userData: UserDashboard | undefined;
}) => {
  const [nickname, setNickname] = useState('');
  const setRenderingHeader = useSetRecoilState(renderingAtom);
  const router = useRouter();

  const { register } = useForm();

  const onSubmitEditProfile = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
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
                    nickname: '',
                    infoMessage: '',
                    github: '',
                    blog: '',
                    jobType: '',
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

  const onBlurNickname = () => {
    inspectNicknameDuplication(userData?.nickname ?? '', nickname);
  };

  useEffect(() => {
    userData?.nickname && setNickname(userData.nickname);
  }, [userData]);

  return (
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
  );
};

export default EditNickName;
