import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../atomsYW';
import { Editor } from '../../components/common/Editor';
import { Seo } from '../../components/common/Seo';

const Ask: NextPage = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const router = useRouter();
  useEffect(() => {
    if (!isLogin) router.back();
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
