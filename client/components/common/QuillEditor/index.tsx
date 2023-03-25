import dynamic from 'next/dynamic';
import { Loader } from '../Loader';
<<<<<<< HEAD:client/components/hyejung/QuillEditor.tsx
import type { ReactQuillProps } from 'react-quill';
import ReactQuill from 'react-quill';
=======
>>>>>>> ce718f293ca7535492d6168f43ad435fbdfaf9ff:client/components/common/QuillEditor/index.tsx

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

type QuillEditorProps = ReactQuillProps & {
  forwardRef: React.RefObject<ReactQuill>;
};

export const QuillEditor = dynamic(
  async () => {
    const { default: RQ } = await import('react-quill');
    const { default: ImageResize } = await import('quill-image-resize');
    RQ.Quill.register('modules/ImageResize', ImageResize);
    return function comp({ forwardRef, ...props }: QuillEditorProps) {
      console.log(props);
      return <RQ ref={forwardRef} formats={formats} {...props} />;
    };
  },
  {
    ssr: false,
    loading: () => (
      <div className="flex justify-center items-center mx-auto py-20">
        <Loader />
      </div>
    ),
  },
);
