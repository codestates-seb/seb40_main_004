import { client } from '@libs/client';
import { useRouter } from 'next/router';
import React from 'react';
import { confirmAlert } from 'react-confirm-alert';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import CancelButton from './CancelButton';
import ChangeButton from './ChangeButton';
import NewPasswordInput from './NewPasswordInput';
import OriginalPasswordInput from './OriginalPasswordInput';

type ErrorProps = {
  errorType: boolean;
  message: string | undefined;
};

type UserCredentialsUpdate = {
  originalPassword?: string;
  newPassword?: string;
  newPasswordCheck?: string;
  infoMessage?: string;
  github?: string;
  jobType?: string;
  blog?: string;
};

const EditPassWord = () => {
  const router = useRouter();
  const {
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm();
  // 비밀번호 변경
  const changePassword = async ({
    originalPassword,
    newPassword,
  }: Pick<UserCredentialsUpdate, 'originalPassword' | 'newPassword'>) => {
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
  };

  const onValid: SubmitHandler<UserCredentialsUpdate> = async ({
    originalPassword,
    newPassword,
    newPasswordCheck,
  }) => {
    const yesButton = {
      label: '예',
      onClick: async () => {
        await changePassword({ originalPassword, newPassword });
      },
    };

    const noButton = {
      label: '아니오',
      onClick: () => {
        return;
      },
    };

    newPassword !== newPasswordCheck
      ? setError(
          'newPasswordCheck',
          { message: '비밀번호가 맞지 않습니다.' },
          { shouldFocus: true },
        )
      : confirmAlert({
          message: '비밀번호를 변경 하시겠습니까?',
          buttons: [yesButton, noButton],
        });
  };

  const ErrorMessage = ({ errorType, message }: ErrorProps) => {
    return errorType ? (
      <p className="font-bold text-red-500">{message}</p>
    ) : null;
  };

  return (
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
            message="비밀번호가 일치하지 않습니다."
          />
        </div>
        <div className="flex gap-8">
          <ChangeButton />
          <CancelButton />
        </div>
      </form>
    </>
  );
};

export default EditPassWord;
