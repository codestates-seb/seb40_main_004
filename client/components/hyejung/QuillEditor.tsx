import dynamic from 'next/dynamic';

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

export const QuillEditor = dynamic(
  async () => {
    const { default: RQ } = await import('react-quill');
    const { default: ImageResize } = await import('quill-image-resize');
    RQ.Quill.register('modules/ImageResize', ImageResize);
    return function comp({ forwardRef, ...props }: any) {
      return (
        <>
          <RQ ref={forwardRef} formats={formats} {...props} />
        </>
      );
    };
  },
  {
    ssr: false,
    loading: () => (
      <p className="flex justify-center items-center mx-auto py-20">
        Loading ...
      </p>
    ),
  },
);