/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-27
 */

import dynamic from 'next/dynamic';
import { Loader } from '../Loader';
import type { ReactQuillProps } from 'react-quill';
import ReactQuill from 'react-quill';

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
