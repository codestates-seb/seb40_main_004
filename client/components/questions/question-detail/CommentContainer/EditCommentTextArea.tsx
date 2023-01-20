import { Button } from '../../../common/Button';
import { useForm, SubmitHandler, SubmitErrorHandler } from 'react-hook-form';
import { client } from '../../../../libs/client';
import { mutate } from 'swr';
import { useFetch } from '../../../../libs/useFetchSWR';
import { CommentResp } from '../../../../types/comment';
import { useCheckClickIsLogin } from '../../../../libs/useCheckIsLogin';

type TextAreaProps = {
  url: string;
  mutateUrl: string;
  commentId: number;
  setIsEdit: any;
  isEdit: boolean;
};

type FormValue = {
  content: string;
};

export const EditCommentTextArea = ({
  url,
  mutateUrl,
  commentId,
  setIsEdit,
}: TextAreaProps) => {
  // answerId 가 있는 경우 댓글 코멘트 작성 api 로 요청
  const { data: currComments } = useFetch(mutateUrl);

  const checkIsLogin = useCheckClickIsLogin();

  const currCommentText = currComments.filter(
    (com: CommentResp) => com.commentId === commentId,
  )[0].content;

  const { register, handleSubmit } = useForm<FormValue>({
    defaultValues: { content: currCommentText },
  });

  // 데이터 요청 관련 함수
  const patchComment = async (data: FormValue) => {
    await client.patch(url, data);
    setIsEdit(false);
    mutate(mutateUrl);
  };

  const onValid: SubmitHandler<FormValue> = (data) => {
    patchComment(data);
  };

  const onInvalid: SubmitErrorHandler<FormValue> = (data) => {
    alert('코멘트를 입력해주세요!');
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
        <Button funcProp={checkIsLogin}>코멘트 수정</Button>
      </div>
    </form>
  );
};
