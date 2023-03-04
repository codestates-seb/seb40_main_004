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

import ReactQuill from 'react-quill';
import { getFileUrl, uploadImg } from '@libs/uploadS3';
import { options } from '@libs/tagOptions';
import EditorLabel from './EditorLabel';

import TitleInputBox from './TitleInputBox';
import FormButtonGroup from './FormButtonGroup';

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

type ArticleIdProps = { data: { articleId: string } };

export const Editor = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
    setError,
  } = useForm<ContentProps>({
    mode: 'onChange',
  });
  const [title, setTitle] = useState('');
  const [tags, setTags] = useState<SelectOption[]>([options[1]]);
  const category = 'QNA';
  const [fileIdList, setFileIdList] = useState<{ fileId: string }[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isArticleEdit, setIsArticleEdit] = useRecoilState(isArticleEditAtom);

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

  const handleSuccess = ({ data }: ArticleIdProps) => {
    setIsArticleEdit((prevState) => ({
      ...prevState,
      isArticleEdit: false,
      title: '',
      content: '',
      articleId: '',
    }));

    setIsSubmitting(false);
    router.push(`questions/${data.articleId}`);
  };

  const handleFailure = () => {
    toast.error('게시글 작성에 실패했습니다...🥲 다시 한 번 확인해주세요!');
    setIsSubmitting(false);
  };

  const onValid: SubmitHandler<ContentProps> = async ({ content }) => {
    try {
      if (!tags.length) {
        setError('tags', {
          type: 'minLength',
          message: '최소 한 개 이상의 태그를 선택해주세요!',
        });
        return;
      }

      setIsSubmitting(true);

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

      const { data }: ArticleIdProps = await client[api](url, payload);
      handleSuccess({ data });
      toast.success('글 작성이 완료되었습니다.');
    } catch (_) {
      handleFailure();
    }
  };

  const handleTitleChange = (event: ChangeEvent<HTMLInputElement>) => {
    setTitle(event.target.value);
  };

  const editorChange = (editorState: string) => {
    setValue('content', editorState);
  };

  const handleCancelClick = () => {
    const cancelConfirm = () => {
      setIsArticleEdit({
        isArticleEdit: false,
        title: '',
        content: '',
        articleId: '',
      });
      toast.success('글 작성이 취소되었습니다.');
      router.push('/questions');
    };

    confirmAlert({
      message: '질문 작성을 취소하시겠어요?',
      buttons: [
        {
          label: 'YES',
          onClick: cancelConfirm,
        },
        {
          label: 'NO',
        },
      ],
    });
  };

  const createFileInput = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    document.body.appendChild(input);
    return input;
  };

  const insertimage = async (quill: ReactQuill | null, url: string) => {
    const range = quill?.getEditorSelection();
    if (!quill || !range) {
      toast.error('Error: range is null.');
      return;
    }

    setTimeout(() => {
      const index = range.index;
      const editor = quill.getEditor();
      editor.insertEmbed(index, 'image', url);
      editor.setSelection({ index: range.index + 1, length: 0 });

      const inputToRemove = document.body.querySelector(
        ':scope > input',
      ) as HTMLInputElement;
      inputToRemove?.remove();
    }, 500);
  };

  const quillRef = useRef<ReactQuill>(null);
  const imageHandler = useCallback(async () => {
    const input = createFileInput();
    input.click();
    input.onchange = async () => {
      if (input.files) {
        const file = input.files[0];
        const { preSignedUrl, fileId } = await getFileUrl();
        await uploadImg(preSignedUrl, file);
        const imageUrl = preSignedUrl.split('png')[0] + 'png';

        fileIdList.push({ fileId });
        setFileIdList((fileIdList) => [...fileIdList, { fileId }]);
        insertimage(quillRef.current, imageUrl);
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
          <EditorLabel htmlFor="제목" title="제목" />
          <ValidationMsg msg={errors.content?.message} />
        </article>
        <ValidationMsg msg={errors.title?.message} />
        <TitleInputBox onChange={handleTitleChange} />
      </section>
      <section className="space-y-3 pb-5 relative">
        <EditorLabel htmlFor="본문" title="본문" />
        <ValidationMsg msg={errors.content?.message} />
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
        <EditorLabel htmlFor="태그" title="태그" />
        <ValidationMsg msg={errors.tags?.message} />
        <Select
          multiple
          options={options}
          tags={tags}
          onChange={(element) => setTags(element)}
        />
      </section>

      {/* 등록 취소 버튼 */}
      <FormButtonGroup onCancelClick={handleCancelClick} />

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
