/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-29
 */

import axios from 'axios';
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
import { useRecoilValue } from 'recoil';
import { categoryAtom } from '../../atomsHS';
import { getFileUrl, uploadImg } from '../../libs/uploadS3';
import { Select, SelectOption } from '../haseung/Select';
import { QuillEditor } from '../hyejung/QuillEditor';

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

  useEffect(() => {
    if (document) register('content', { required: true });
  }, [register]);

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

  const onValid = ({ title, content }: ContentProps) => {
    setIsSubmitting(true);
    const files = fileIdList[0]?.fileId;
    axios
      .post(
        `/api/articles`,
        { title, content, category, files, tags },
        {
          headers: {
            'Content-Type': `application/json`,
            Authorization: `${localStorage.getItem('accessToken')}`,
          },
        },
      )
      .then((res) => {
        setIsSubmitting(false);
        router.push(`questions/${res.data.articleId}`);
      })
      .catch((error) => {
        console.error('error', error);
      });
  };

  const handleTitleChange = (event: ChangeEvent<HTMLInputElement>) =>
    setTitle(event.target.value);

  const editorChange = (editorState: string) => {
    setValue('content', editorState);
  };

  const handleCancelClick = () => {
    if (confirm('질문 작성을 취소하겠습니까?')) router.push('/questions');
  };

  const editorContent = watch('content');
  return (
    <form onSubmit={handleSubmit(onValid)}>
      <div
        className="ml-3"
        dangerouslySetInnerHTML={{ __html: editorContent }}
      />
      <label htmlFor="제목" className="font-bold ml-2 flex py-2 px-2">
        제목
      </label>
      <input
        value={title}
        {...register('title', {
          minLength: {
            value: 5,
            message: '제목은 5글자 이상으로 해주세요.',
          },
        })}
        onChange={handleTitleChange}
        type="text"
        className="w-[97%] border-2 px-2 py-1 leading-loose mx-auto flex justify-center overflow-x-hidden"
        placeholder="제목을 입력해주세요!"
      />
      <p className="font-bold text-red-500 ml-4 mt-2">
        {errors.title?.message}
      </p>
      <label htmlFor="본문" className="font-bold ml-2 flex py-2 px-2">
        본문
      </label>
      <QuillEditor
        className="h-96 w-[97%] mx-auto py-1"
        value={editorContent}
        modules={modules}
        onChange={editorChange}
        bounds="#editor"
        forwardRef={quillRef}
      />
      <label htmlFor="태그" className="font-bold ml-2 flex py-2 mt-10 px-2">
        태그
      </label>
      <Select
        multiple
        options={options}
        tags={tags}
        onChange={(element) => setTags(element)}
      />
      <article className="flex justify-center">
        <input
          className="justify-center mx-2 my-20 bg-main-yellow px-4 py-2 rounded-full cursor-pointer hover:bg-main-orange"
          type="submit"
          value="등록"
        />
        <input
          onClick={handleCancelClick}
          className="justify-center mx-2 my-20 bg-background-gray px-4 py-2 rounded-full cursor-pointer hover:bg-main-gray"
          type="submit"
          value="취소"
        />
      </article>
      <p className="text-center relative bottom-10 font-bold text-xl">
        {isSubmitting ? 'Loading...' : null}
      </p>
    </form>
  );
};
