/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-28
 */
import { useRouter } from 'next/router';
import { Button } from '../common/Button';
import { useForm, SubmitHandler, SubmitErrorHandler } from 'react-hook-form';
import { client } from '../../libs/client';
import { mutate } from 'swr';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { isLoginAtom, renderingAtom } from '../../atomsYW';
import { useCheckClickIsLogin } from '../../libs/useCheckIsLogin';

type TextAreaProps = {
  answerId?: number;
};

type FormValue = {
  content: string;
};

export const CommentTextArea = ({ answerId }: TextAreaProps) => {
  // router, id
  const router = useRouter();
  const { articleId } = router.query;
  const setRenderingHeader = useSetRecoilState(renderingAtom);
  const isLogin = useRecoilValue(isLoginAtom);
  const checkIsLogin = useCheckClickIsLogin();

  const { register, handleSubmit, watch, setValue } = useForm<FormValue>();

  if (watch().content?.length >= 1 && !isLogin) {
    if (confirm('로그인이 필요한 서비스 입니다. 바로 로그인 하시겠어요?')) {
      router.push('/login');
    }
  }

  // answerId 가 있는 경우 댓글 코멘트 작성 api 로 요청
  const url = answerId
    ? `/api/answers/${answerId}/comments`
    : `/api/articles/${articleId}/comments`;

  // 데이터 요청 관련 함수
  const postComment = async (data: FormValue) => {
    await client.post(url, data);
    mutate(url)
      .then(() => {
        setRenderingHeader((prev) => !prev);
      })
      .catch((err) => {
        alert('코멘트 등록에 실패했습니다...!');
        console.log(err);
      });
  };

  const onValid: SubmitHandler<FormValue> = (data) => {
    if (isLogin) {
      postComment(data);
      setValue('content', '');
    } else {
      checkIsLogin();
    }
  };

  const onInvalid: SubmitErrorHandler<FormValue> = () => {
    if (isLogin) {
      alert('코멘트를 입력해주세요!');
    } else {
      checkIsLogin();
    }
  };

  return (
    <form
      className="flex flex-col space-y-3"
      onSubmit={handleSubmit(onValid, onInvalid)}
    >
      <textarea
        className="w-full rounded-[20px] border p-4 text-sm  focus:outline-main-gray"
        rows={4}
        {...register('content', {
          required: '코멘트를 입력해주세요!',
        })}
      />
      <div className="flex justify-end">
        <Button funcProp={checkIsLogin}>코멘트 등록</Button>
      </div>
    </form>
  );
};
