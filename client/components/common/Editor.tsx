/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-04(박혜정)
 */

import { useRouter } from 'next/router';
import {
  ChangeEvent,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { useForm } from 'react-hook-form';
import { useRecoilState, useRecoilValue } from 'recoil';
import { isArticleEditAtom } from '../../atomsHJ';
import { categoryAtom } from '../../atomsHS';
import { client } from '../../libs/client';
import { getFileUrl, uploadImg } from '../../libs/uploadS3';
import { Select, SelectOption } from '../haseung/Select';
import { QuillEditor } from '../hyejung/QuillEditor';
import { Loader } from './Loader';

type ContentProps = {
  title: string;
  content: string;
  tags: {
    tagId: number;
    name: string;
  }[];
  fileId: {
    fileId: number[];
  };
};

const options = [
  { tagId: 0, name: 'JAVA' },
  { tagId: 1, name: 'C' },
  { tagId: 2, name: 'NODE' },
  { tagId: 3, name: 'SPRING' },
  { tagId: 4, name: 'REACT' },
  { tagId: 5, name: 'JAVASCRIPT' },
  { tagId: 6, name: 'CPLUSCPLUS' },
  { tagId: 7, name: 'CSHOP' },
  { tagId: 8, name: 'NEXT' },
  { tagId: 9, name: 'NEST' },
  { tagId: 10, name: 'PYTHON' },
  { tagId: 11, name: 'SWIFT' },
  { tagId: 12, name: 'KOTLIN' },
  { tagId: 13, name: 'CSS' },
  { tagId: 14, name: 'HTML' },
  { tagId: 15, name: 'AWS' },
  { tagId: 16, name: 'REDUX' },
  { tagId: 17, name: 'SCALA' },
  { tagId: 18, name: 'GO' },
  { tagId: 19, name: 'TYPESCRIPT' },
];

export const Editor = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<ContentProps>({
    mode: 'onChange',
  });
  const [title, setTitle] = useState('');
  const [tags, setTags] = useState<SelectOption[]>([options[1]]);
  const category = useRecoilValue(categoryAtom);
  const [fileIdList, setFileIdList] = useState<any>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isArticleEdit, setIsArticleEdit] = useRecoilState(isArticleEditAtom);

  useEffect(() => {
    if (isArticleEdit.isArticleEdit) {
      setValue('title', isArticleEdit.title);
      setValue('content', isArticleEdit.content);
    }
  }, []);

  useEffect(() => {
    if (document) register('content', { required: true });
  }, [register]);

  const editorContent = watch('content');

  const onValid = ({ title, content }: ContentProps) => {
    setIsSubmitting(true);
    if (isArticleEdit.isArticleEdit) {
      client
        .patch(`/api/articles/${isArticleEdit.articleId}`, {
          title,
          content,
          fileId: fileIdList,
          tags,
        })
        .then((res) => {
          setIsSubmitting(true);
          setIsArticleEdit({
            isArticleEdit: false,
            title: '',
            content: '',
            articleId: '',
          });
          router.push(`questions/${res.data.articleId}`);
        })
        .catch((error) => {
          console.error('error', error);
          alert('게시글 수정에 실패했습니다...🥲');
          console.log(
            `title:${title}, content:${content}, fileId:${fileIdList}, tags:${tags}`,
          );
        });
    } else {
      client
        .post(`/api/articles`, {
          title,
          content,
          category,
          fileId: fileIdList,
          tags,
        })
        .then((res) => {
          setIsSubmitting(false);
          router.push(`questions/${res.data.articleId}`);
        })
        .catch((error) => {
          console.error('error', error);
          // alert('게시글 작성에 실패했습니다...🥲 다시 한 번 확인해주세요!');
          if (Boolean(tags)) alert('태그를 입력해주세요');
        });
    }
  };

  const handleTitleChange = (event: ChangeEvent<HTMLInputElement>) =>
    setTitle(event.target.value);

  const editorChange = (editorState: string) => {
    setValue('content', editorState);
  };

  const handleCancelClick = () => {
    if (confirm('질문 작성을 취소하시겠어요?')) {
      router.push('/questions');
    }
  };

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

        const { preSignedUrl, fileId } = await getFileUrl();
        await uploadImg(preSignedUrl, file);
        const imageUrl = preSignedUrl.split('png')[0] + 'png';

        fileIdList.push({ fileId });
        const newFiledIdList = fileIdList;
        setFileIdList(newFiledIdList);

        const range = quillRef.current.getEditorSelection();
        setTimeout(() => {
          quillRef.current
            .getEditor()
            .insertEmbed(range.index, 'image', imageUrl);
          quillRef.current.getEditor().setSelection(range.index + 1);
          const myInput = document.body.querySelector(
            ':scope > input',
          ) as HTMLInputElement;
          myInput.remove();
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
    <form onSubmit={handleSubmit(onValid)} className="h-full p-8 space-y-8">
      <section className="space-y-3">
        <label htmlFor="제목" className="font-bold flex">
          제목
        </label>
        <input
          {...register('title', {
            minLength: {
              value: 5,
              message: '제목은 5글자 이상으로 해주세요.',
            },
          })}
          onChange={handleTitleChange}
          type="text"
          className="border-2 px-2 py-1 leading-loose flex w-full justify-center rounded-md"
          placeholder="제목을 입력해주세요!"
        />
        <p className="font-bold text-red-500">{errors.title?.message}</p>
      </section>
      <section className="space-y-3">
        <label htmlFor="본문" className="font-bold flex">
          본문
        </label>
        <QuillEditor
          className="h-96 w-full mx-auto py-1"
          value={editorContent}
          modules={modules}
          onChange={editorChange}
          bounds="#editor"
          forwardRef={quillRef}
        />
      </section>
      <section className="space-y-3 pt-10">
        <label htmlFor="태그" className="font-bold flex">
          태그
        </label>
        <Select
          multiple
          options={options}
          tags={tags}
          onChange={(element) => setTags(element)}
        />
      </section>

      <article className="flex justify-center py-16">
        <input
          onClick={handleCancelClick}
          className="justify-center mx-2 bg-main-gray bg-opacity-80 px-4 py-2 rounded-full cursor-pointer hover:bg-main-gray hover:bg-opacity-100"
          type="submit"
          value="취소"
        />
        <input
          className="justify-center mx-2 bg-main-yellow bg-opacity-80 px-4 py-2 rounded-full cursor-pointer hover:bg-main-yellow hover:bg-opacity-100 "
          type="submit"
          value="등록"
        />
      </article>
      <p className="text-center relative bottom-10 font-bold text-xl">
        {isSubmitting ? (
          <>
            <Loader /> <span>등록 중....</span>
          </>
        ) : null}
      </p>
    </form>
  );
};
