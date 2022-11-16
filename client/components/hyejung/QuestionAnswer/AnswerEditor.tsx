import dynamic from 'next/dynamic';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Button } from '../../common/Button';

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

type Content = {
  content: string;
};

export const AnswerEditor = () => {
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
    <div className="mt-10">
      <form onSubmit={handleSubmit(onValid)}>
        <QuillNoSSRWrapper
          className="h-96"
          value={editorContent}
          modules={modules}
          formats={formats}
          onChange={editorChange}
          bounds="#editor"
        />
      </form>
      <article className="mt-28 flex justify-end">
        <Button>답변 등록하기</Button>
      </article>
    </div>
  );
};
