/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-27
 */
import { useRouter } from 'next/router';

import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useForm, SubmitHandler, SubmitErrorHandler } from 'react-hook-form';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import { isAnswerEditAtom, isAnswerPostedAtom } from '../../atomsHJ';
import { client } from '../../libs/client';
import { useFetch } from '../../libs/useFetchSWR';
import { mutate as patchMutate } from 'swr';
import { isLoginAtom } from '../../atomsYW';
import { getFileUrl, uploadImg } from '../../libs/uploadS3';
import { QuillEditor } from './QuillEditor';

type FormValue = {
  content: string;
};

export const AnswerEditor = () => {
  const router = useRouter();
  const { articleId } = router.query;

  const isAnswerPosted = useSetRecoilState(isAnswerPostedAtom);
  const [isAnserEdit, setIsAnswerEdit] = useRecoilState(isAnswerEditAtom);
  const isLogin = useRecoilValue(isLoginAtom);

  const { data: currAnswers, mutate } = useFetch(
    `/api/articles/${articleId}/answers?page=1&size=5`,
  );
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

  // 답변 수정 모드일 경우 동작!
  useEffect(() => {
    if (isAnserEdit.isEdit) {
      window.scrollTo({ top: 99999, behavior: 'smooth' });
      setValue('content', isAnserEdit.payload);
    }
  }, [isAnserEdit]);

  // 화면에 출력할 사용자 입력 데이터
  const editorContent = watch('content');
  const handleChange = (value: string) => {
    if (!isLogin) {
      if (confirm('로그인이 필요한 서비스 입니다. 바로 로그인 하시겠어요?')) {
        router.push('/login');
      }
    }
    setValue('content', value === '<p><br></p>' ? '' : value);
    trigger('content');
  };

  // 데이터 요청 관련 함수
  const postAnswer = async (data: FormValue) => {
    const response = await client.post(
      `/api/articles/${articleId}/answers`,
      data,
    );
    const newAnswers = response.data;
    mutate({ currAnswers, ...newAnswers }, { revalidate: false })
      .then(() => {
        alert('답변이 성공적으로 등록되었습니다!');
        isAnswerPosted(true);
        setValue('content', '');
        trigger('content');
      })
      .catch((err) => {
        console.log(err);
        alert('답변 등록에 실패했습니다...!');
      });
  };
  const patchAnswer = async (data: FormValue) => {
    const response = await client.patch(
      `/api/articles/${articleId}/answers/${isAnserEdit.answerId}`,
      data,
    );
    const updatedAnswer = response.data;
    patchMutate(
      `/api/articles/${articleId}/answers?page=${isAnserEdit.answerPage}&size=5`,
      { currAnswers, ...updatedAnswer },
      { revalidate: false },
    )
      .then(() => {
        alert('답변을 수정하였습니다!');
        isAnswerPosted(true);
        setValue('content', '');
        trigger('content');
        setIsAnswerEdit({ ...isAnserEdit, isEdit: false });
      })
      .catch((err) => {
        console.log(err);
        alert('답변 수정에 실패했습니다...!');
      });
  };
  const onValid: SubmitHandler<FormValue> = async (data) => {
    if (isAnserEdit.isEdit) patchAnswer(data);
    else postAnswer(data);
  };
  const onInvalid: SubmitErrorHandler<FormValue> = (data) => {
    alert(data.content?.message);
  };

  // Quill 에디터 관련 코드
  const quillRef = useRef<any>(null);
  const imageHandler = useCallback(async () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    document.body.appendChild(input);

    input.click();
    input.onchange = async () => {
      if (input.files) {
        const file = input.files[0];

        // 서버로부터 받아온 s3 업로드용 url 과 fielId
        const { preSignedUrl } = await getFileUrl();
        // 해당 url 로 put 요청 보내기
        await uploadImg(preSignedUrl, file);

        const imageUrl = preSignedUrl.split('png')[0] + 'png';

        const range = quillRef.current.getEditorSelection();
        setTimeout(() => {
          quillRef.current
            .getEditor()
            .insertEmbed(range.index, 'image', imageUrl);
          quillRef.current.getEditor().setSelection(range.index + 1);
          // document.body!.querySelector(':scope > input').remove();
        }, 500);
      }
    };
  }, []);
  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
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
        handlers: {
          image: imageHandler,
        },
      },
      clipboard: {
        matchVisual: true,
      },
      ImageResize: {
        modules: ['Resize', 'DisplaySize', 'Toolbar'],
      },
    }),
    [],
  );

  return (
    <div className="mt-5">
      <form onSubmit={handleSubmit(onValid, onInvalid)}>
        <QuillEditor
          className="h-96"
          value={editorContent}
          onChange={handleChange}
          bounds="#editor"
          modules={modules}
          forwardRef={quillRef}
        />
        <article className="mt-28 flex justify-end">
          <input
            className="bg-main-yellow text-[16px] px-5 py-[6px] rounded-full cursor-pointer"
            type="submit"
            value={isAnserEdit.isEdit ? '답변 수정하기' : '답변 등록하기'}
          />
        </article>
      </form>
    </div>
  );
};
