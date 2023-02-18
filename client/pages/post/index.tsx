import { GetServerSideProps, NextPage } from 'next';

import { Editor } from '@components/common/QuillEditor/Editor';
import { Seo } from '@components/common/Seo';
import { useEffect } from 'react';
import { useCheckClickIsLogin } from '@libs/useCheckIsLogin';

const Ask: NextPage = () => {
  const checkIsLogin = useCheckClickIsLogin();
  useEffect(() => {
    checkIsLogin();
  }, []);
  return (
    <>
      <Seo title="질문 작성" />
      <Editor />
    </>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const content = context.req.url?.split('/')[1];
  return {
    props: {
      content,
    },
  };
};

export default Ask;
