/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-25
 */
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';

import { useEffect } from 'react';
import { useForm, SubmitHandler, SubmitErrorHandler } from 'react-hook-form';
import { useRecoilState } from 'recoil';
import { useSWRConfig } from 'swr';
import { isAnswerPostAtom } from '../../atomsHJ';
import { client } from '../../libs/client';
import { useFetch } from '../../libs/useFetchSWR';

const QuillNoSSRWrapper = dynamic(() => import('react-quill'), {
  ssr: false,
  loading: () => (
    <p className="flex justify-center items-center mx-auto py-20">
      Loading ...
    </p>
  ),
});

const modules = {
  toolbar: [
    [{ header: '1' }, { header: '2' }, { font: [] }],
    [{ size: [] }],
    ['bold', 'italic', 'underline', 'strike', 'blockquote', 'code-block'],
    [
      { list: 'ordered' },
      { list: 'bullet' },
      { indent: '-1' },
      { indent: '+1' },
    ],
    ['link', 'image', 'video'],
    ['clean'],
  ],
  clipboard: {
    matchVisual: true,
  },
};

const formats = [
  'header',
  'font',
  'size',
  'bold',
  'italic',
  'underline',
  'strike',
  'blockquote',
  'list',
  'bullet',
  'indent',
  'link',
  'image',
  'video',
  'code-block',
];

type FormValue = {
  content: string;
};

export const AnswerEditor = () => {
  const router = useRouter();
  const { articleId } = router.query;

  const { register, handleSubmit, watch, setValue, trigger } =
    useForm<FormValue>({
      mode: 'onChange',
    });

  // register 등록 코드
  useEffect(() => {
    if (document) {
      register('content', {
        required: '답변을 입력해주세요!',
        minLength: {
          message: '답변을 최소 15글자 이상 입력해주세요.',
          value: 15,
        },
      });
    }
  }, [register]);

  // 화면에 출력할 입력 데이터
  const editorContent = watch('content');

  // 유저의 입력 데이터를 content 라는 키로 등록!
  // trigger 함수를 사용해 content 키에 대한 변경이 있음을 알린다.
  const handleChange = (value: string) => {
    setValue('content', value === '<p><br></p>' ? '' : value);
    trigger('content');
  };

  const { data: currAnswers, mutate } = useFetch(
    `/api/articles/${articleId}/answers?page=1&size=5`,
  );

  const [isAnswerPost, setIsAnswerPost] = useRecoilState(isAnswerPostAtom);

  // form 데이터가 유효하다면 요청
  const onValid: SubmitHandler<FormValue> = async (data) => {
    const response = await client.post(
      `/api/articles/${articleId}/answers`,
      data,
    );
    const newAnswers = response.data;
    const newAnswerId = response.data.data.answerId;
    mutate({ currAnswers, ...newAnswers }, { revalidate: false }).then(() => {
      alert('답변이 성공적으로 등록되었습니다!');
      console.log(response);
      setIsAnswerPost({
        isAnswerPost: true,
        answerId: newAnswerId,
      });
      setValue('content', '');
      trigger('content');
    });
  };

  const onInvalid: SubmitErrorHandler<FormValue> = (data) => {
    alert(data.content?.message);
  };

  return (
    <div className="mt-5">
      <form onSubmit={handleSubmit(onValid, onInvalid)}>
        <QuillNoSSRWrapper
          className="h-96"
          value={editorContent}
          modules={modules}
          formats={formats}
          onChange={handleChange}
          bounds="#editor"
        />
        <article className="mt-28 flex justify-end">
          <input
            className="bg-main-yellow text-[16px] px-5 py-[6px] rounded-full cursor-pointer"
            type="submit"
            value="답변 등록하기"
          />
        </article>
      </form>
    </div>
  );
};
