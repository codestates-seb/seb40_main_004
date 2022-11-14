/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import dynamic from 'next/dynamic';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';

const QuillNoSSRWrapper = dynamic(import('react-quill'), {
  ssr: false,
  loading: () => <p>Loading ...</p>,
});

const modules = {
  toolbar: [
    [{ header: '1' }, { header: '2' }, { font: [] }],
    [{ size: [] }],
    ['bold', 'italic', 'underline', 'strike', 'blockquote'],
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
    matchVisual: false,
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
];

type Content = {
  content: string;
};

export const Editor = () => {
  const { register, handleSubmit, watch, setValue } = useForm<Content>();

  useEffect(() => {
    if (document) {
      register('content', { required: true });
    }
  }, [register]);

  const onValid = (data: Content) => {
    console.log(data);
  };
  const editorChange = (editorState: string) => {
    setValue('content', editorState);
  };

  const editorContent = watch('content');
  return (
    <section>
      <QuillNoSSRWrapper
        className="h-96"
        value={editorContent}
        modules={modules}
        formats={formats}
        onChange={editorChange}
      />
      <article className="flex justify-center">
        <input
          className="justify-center mx-2 my-20 bg-background-gray px-4 py-2 rounded-full cursor-pointer hover:bg-main-orange"
          type="submit"
          onClick={handleSubmit(onValid)}
          value="등록"
        />
        <input
          className="justify-center mx-2 my-20 bg-[#F2F2F2]  px-4 py-2 rounded-full cursor-pointer hover:bg-main-gray"
          type="submit"
          onClick={handleSubmit(onValid)}
          value="취소"
        />
      </article>
    </section>
  );
};
