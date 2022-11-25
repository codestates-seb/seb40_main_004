/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-23
 */
import { useRouter } from 'next/router';
import { Button } from '../common/Button';
import { useForm, SubmitHandler, SubmitErrorHandler } from 'react-hook-form';
import { client } from '../../libs/client';

type TextAreaProps = {
  answerId?: string;
};

type FormValue = {
  comment: string;
};

export const CommentTextArea = ({ answerId }: TextAreaProps) => {
  // router, id
  const router = useRouter();
  const { articleId } = router.query;

  // form validation
  const { register, handleSubmit } = useForm<FormValue>();

  const onValid: SubmitHandler<FormValue> = ({ comment }) => {
    // answerId 가 있는 경우 댓글 코멘트 작성 api 로 요청
    let url = answerId
      ? `/api/articles/${articleId}/answers/${answerId}/comments`
      : `/api/articles/${articleId}/comments`;

    // client
    //   .post(url, { content: comment })
    //   .then((res) => console.log(res))
    //   .catch((err) => console.error(err));
  };

  const onInvalid: SubmitErrorHandler<FormValue> = ({ comment }) => {
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
        {...register('comment', {
          required: '코멘트를 입력해주세요!',
        })}
      />
      <div className="flex justify-end">
        <Button>코멘트 등록</Button>
      </div>
    </form>
  );
};
