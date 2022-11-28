/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-15
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
import { categoryAtom, tagIdAtom } from '../../atomsHS';
import { getFileUrl, uploadImg } from '../../libs/uploadS3';
import { QuillEditor } from '../hyejung/QuillEditor';

type Content = {
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

export const Editor = () => {
  const router = useRouter();
  const { articleId } = router.query;
  const { register, handleSubmit, watch, setValue } = useForm<Content>({
    mode: 'onChange',
  });
  const [title, setTitle] = useState('');

  const category = useRecoilValue(categoryAtom);
  const tagId = useRecoilValue(tagIdAtom);
  const [fileIdList, setFileIdList] = useState<any>([]);

  const quillRef = useRef<any>(null);

  useEffect(() => {
    if (document) {
      register('content', { required: true });
    }
  }, [register]);

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

        const newFileIdList = fileIdList;
        setFileIdList(newFileIdList);

        const range = quillRef.current?.getEditorSelection();
        setTimeout(() => {
          quillRef.current
            ?.getEditor()
            .insertEmbed(range.index, 'image', imageUrl);
          quillRef.current?.getEditor().setSelection(range.index + 1);
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
    }),
    [],
  );

  const onValid = ({ title, content, tags, fileId }: Content) => {
    const files = fileIdList[0]?.fileId;
    axios
      .post(
        `${process.env.NEXT_PUBLIC_API_URL}/articles`,
        { title, content, category, files, tags, tagId },
        {
          headers: {
            'Content-Type': `application/json`,
            Authorization: `${localStorage.getItem('accessToken')}`,
          },
        },
      )
      .then(() => {
        router.push(`/`);
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
        {...register('title')}
        onChange={handleTitleChange}
        type="text"
        className="w-full border-2 px-2 py-1 justify-center leading-loose"
        placeholder="제목을 입력해주세요!"
      />
      <QuillEditor
        className="h-96"
        value={editorContent}
        modules={modules}
        onChange={editorChange}
        bounds="#editor"
      />

      <article className="flex justify-center">
        <input
          className="justify-center mx-2 my-20 bg-main-yellow px-4 py-2 rounded-full cursor-pointer hover:bg-main-orange"
          type="submit"
          value="등록"
        />
        <input
          className="justify-center mx-2 my-20 bg-background-gray px-4 py-2 rounded-full cursor-pointer hover:bg-main-gray"
          type="submit"
          value="취소"
        />
      </article>
    </form>
  );
};
