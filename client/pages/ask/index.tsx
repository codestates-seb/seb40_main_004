import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../atomsYW';
import { Editor } from '../../components/common/Editor';
import { TitleProps } from '../../libs/interfaces';

const Ask: NextPage<TitleProps> = ({ title = '질문 작성하기' }) => {
  const isLogin = useRecoilValue(isLoginAtom);
  const router = useRouter();
  useEffect(() => {
    if (!isLogin) router.back();
  }, []);
  return (
    <>
      <Head>
        <title>{title}</title>
      </Head>
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
