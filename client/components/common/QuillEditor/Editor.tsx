import { useRouter } from 'next/router';
import {
  ChangeEvent,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { confirmAlert } from 'react-confirm-alert';
import { useForm, SubmitHandler } from 'react-hook-form';
import { toast } from 'react-toastify';
import { useRecoilState } from 'recoil';
import { Select, SelectOption } from './Select';
import { QuillEditor } from '.';
import { Loader } from '../Loader';
import { ValidationMsg } from '../ValildationMsg';

import { isArticleEditAtom } from '@atoms/articleAtom';

import { client } from '@libs/client';
import { getFileUrl, uploadImg } from '@libs/uploadS3';
import ReactQuill from 'react-quill';

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
  { tagId: 6, name: 'CPLUSPLUS' },
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
  const category = 'QNA';
  const [fileIdList, setFileIdList] = useState<{ fileId: string }[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isArticleEdit, setIsArticleEdit] = useRecoilState(isArticleEditAtom);
  const [tagsError, setTagsError] = useState('');

  // 질문글 수정을 통해 넘어왔다면 해당 데이터로 초기값 셋팅
  useEffect(() => {
    if (isArticleEdit.isArticleEdit) {
      setValue('title', isArticleEdit.title);
      setValue('content', isArticleEdit.content);
    }
  }, []);

  // 에디터 콘텐츠 register 부분
  useEffect(() => {
    if (document)
      register('content', {
        required: '내용을 입력해주세요!',
        minLength: {
          message: '내용은 최소 5글자 이상 작성해주세요!🤭',
          value: 5,
        },
      });
  }, [register]);

  const editorContent = watch('content');

  const onValid: SubmitHandler<ContentProps> = async ({ title, content }) => {
    try {
      if (!tags.length) {
        setTagsError('최소 한 개 이상의 태그를 선택해주세요!');
        return;
      }

      setIsSubmitting(true);
      setTagsError('');

      const isEditing = isArticleEdit.isArticleEdit;
      const api = isEditing ? 'patch' : 'post';
      const url = isEditing
        ? `/api/articles/${isArticleEdit.articleId}`
        : `/api/articles`;
      const payload = {
        title,
        content,
        // isEditing이 true일 경우 fileId를 키로 갖고 아니면 category를 키로 가짐
        // isEditing이 true면 fileIdList를 value로 갖고 아니면 category를 value로 가짐
        [isEditing ? 'fileId' : 'category']: isEditing ? fileIdList : category,
        tags,
      };

      const { data } = await client[api](url, payload);
      setIsArticleEdit((prevState) => ({
        ...prevState,
        isArticleEdit: false,
        title: '',
        content: '',
        articleId: '',
      }));
      setIsSubmitting(false);
      router.push(`questions/${data.articleId}`);
    } catch (error) {
      console.error(error);
      toast.error('게시글 작성에 실패했습니다...🥲 다시 한 번 확인해주세요!');
    }
  };

  const handleTitleChange = (event: ChangeEvent<HTMLInputElement>) =>
    setTitle(event.target.value);

  const editorChange = (editorState: string) => {
    setValue('content', editorState);
  };

  const handleCancelClick = () => {
    confirmAlert({
      message: '질문 작성을 취소하시겠어요?',
      buttons: [
        {
          label: 'YES',
          onClick: () => {
            setIsArticleEdit({
              isArticleEdit: false,
              title: '',
              content: '',
              articleId: '',
            });
            toast.success('글 작성이 취소되었습니다.');
            router.push('/questions');
          },
        },
        {
          label: 'NO',
        },
      ],
    });
  };

  const quillRef = useRef<ReactQuill>(null);
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

        const range = quillRef.current?.getEditorSelection();
        if (quillRef.current && range) {
          if (typeof range.index === 'number')
            setTimeout(() => {
              const index = range.index;
              quillRef.current
                ?.getEditor()
                .insertEmbed(index, 'image', imageUrl);
              quillRef.current
                ?.getEditor()
                .setSelection({ index: range.index + 1, length: 0 });
              const myInput = document.body.querySelector(
                ':scope > input',
              ) as HTMLInputElement;
              myInput.remove();
            }, 500);
        } else {
          console.error('Error: range is null.');
        }
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
    <form onSubmit={handleSubmit(onValid)} className="h-full p-8">
      <section className="space-y-3 pb-5">
        <article className="flex items-baseline space-x-3">
          <label htmlFor="제목" className="font-bold flex">
            제목
          </label>
          <ValidationMsg msg={errors.title?.message} />
        </article>
        <input
          {...register('title', {
            required: '제목을 입력해주세요!',
            minLength: {
              value: 5,
              message: '제목은 최소 5글자 이상 작성해주세요!🤭',
            },
          })}
          onChange={handleTitleChange}
          type="text"
          className="border-2 px-2 py-1 leading-loose flex w-full justify-center rounded-md"
          placeholder="제목을 입력해주세요!"
        />
      </section>
      <section className="space-y-3 pb-5 relative">
        <article className="flex items-baseline space-x-3">
          <label htmlFor="본문" className="font-bold flex">
            본문
          </label>
          <ValidationMsg msg={errors.content?.message} />
        </article>
        <QuillEditor
          className="h-[45vh] w-full mx-auto pb-10"
          value={editorContent}
          modules={modules}
          onChange={editorChange}
          bounds="#editor"
          forwardRef={quillRef}
        />
      </section>
      {/* 태그 */}
      <section className="space-y-3 pt-10">
        <article className="flex items-baseline space-x-3">
          <label htmlFor="태그" className="font-bold flex">
            태그
          </label>
          <ValidationMsg msg={tagsError} />
        </article>
        <Select
          multiple
          options={options}
          tags={tags}
          onChange={(element) => setTags(element)}
        />
      </section>

      {/* 등록 취소 버튼 */}
      <article className="flex justify-center py-24">
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

      {/* 로딩 컴포넌트 */}
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
